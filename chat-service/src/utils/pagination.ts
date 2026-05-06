export type PageResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
};

export function toPageResponse<T>(content: T[], page: number, size: number, totalElements: number): PageResponse<T> {
  const totalPages = size === 0 ? 0 : Math.ceil(totalElements / size);
  return {
    content,
    page,
    size,
    totalElements,
    totalPages,
    last: page + 1 >= totalPages
  };
}
