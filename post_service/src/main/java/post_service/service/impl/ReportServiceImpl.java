package post_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.dto.request.ReportCommentRequest;
import post_service.dto.request.ReportPostRequest;
import post_service.dto.request.UpdateReportStatusRequest;
import post_service.dto.response.ReportResponse;
import post_service.entity.*;
import post_service.enums.CommentStatus;
import post_service.enums.PostStatus;
import post_service.exception.*;
import post_service.mapper.ReportMapper;
import post_service.repository.*;
import post_service.service.ReportService;
import post_service.util.PageResponseConverter;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final ReportMapper reportMapper;
    private final PageResponseConverter pageResponseConverter;

    @Override
    @Transactional
    public DataResponseMessage<ReportResponse> reportPost(Long authUserId, Long postId, ReportPostRequest request) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));
        if (post.getAuthUserId().equals(authUserId)) {
            throw new PostAccessDeniedException("You cannot report your own post.");
        }
        if (postReportRepository.existsByPostIdAndReporterAuthUserId(postId, authUserId)) {
            throw new PostAlreadyReportedException();
        }
        PostReport report = PostReport.builder()
                .post(post).reporterAuthUserId(authUserId)
                .reason(request.getReason()).description(request.getDescription()).build();
        report = postReportRepository.save(report);
        post.incrementReportCount();
        postRepository.save(post);
        return DataResponseMessage.success("Post reported successfully.", reportMapper.toPostReportResponse(report));
    }

    @Override
    @Transactional
    public DataResponseMessage<ReportResponse> reportComment(Long authUserId, Long commentId, ReportCommentRequest request) {
        Comment comment = commentRepository.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (comment.getAuthUserId().equals(authUserId)) {
            throw new CommentAccessDeniedException("You cannot report your own comment.");
        }
        if (commentReportRepository.existsByCommentIdAndReporterAuthUserId(commentId, authUserId)) {
            throw new CommentAlreadyReportedException();
        }
        CommentReport report = CommentReport.builder()
                .comment(comment).reporterAuthUserId(authUserId)
                .reason(request.getReason()).description(request.getDescription()).build();
        report = commentReportRepository.save(report);
        comment.incrementReportCount();
        commentRepository.save(comment);
        return DataResponseMessage.success("Comment reported successfully.", reportMapper.toCommentReportResponse(report));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<ReportResponse>> getPostReports(int page, int size) {
        Page<PostReport> reports = postReportRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<ReportResponse> mapped = reports.map(reportMapper::toPostReportResponse);
        return DataResponseMessage.success("Post reports retrieved.", pageResponseConverter.toPageResponse(mapped));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<ReportResponse>> getCommentReports(int page, int size) {
        Page<CommentReport> reports = commentReportRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<ReportResponse> mapped = reports.map(reportMapper::toCommentReportResponse);
        return DataResponseMessage.success("Comment reports retrieved.", pageResponseConverter.toPageResponse(mapped));
    }

    @Override
    @Transactional
    public DataResponseMessage<ReportResponse> updatePostReportStatus(Long reportId, UpdateReportStatusRequest request) {
        PostReport report = postReportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        report.setStatus(request.getStatus());
        report.setReviewedAt(LocalDateTime.now());
        report = postReportRepository.save(report);
        return DataResponseMessage.success("Post report status updated.", reportMapper.toPostReportResponse(report));
    }

    @Override
    @Transactional
    public DataResponseMessage<ReportResponse> updateCommentReportStatus(Long reportId, UpdateReportStatusRequest request) {
        CommentReport report = commentReportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        report.setStatus(request.getStatus());
        report.setReviewedAt(LocalDateTime.now());
        report = commentReportRepository.save(report);
        return DataResponseMessage.success("Comment report status updated.", reportMapper.toCommentReportResponse(report));
    }
}
