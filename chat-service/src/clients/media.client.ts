import axios from "axios";
import { env } from "../config/env";

export type MediaSummary = {
  mediaId: number;
  url: string;
  mediaType: string;
  fileName?: string | null;
  size?: number | null;
  duration?: number | null;
  thumbnailUrl?: string | null;
};

const client = axios.create({
  baseURL: env.MEDIA_SERVICE_URL,
  timeout: 4000
});

export const MediaClient = {
  async validateMediaIds(mediaIds: number[]): Promise<boolean> {
    const response = await client.post("/v1/api/media/internal/validate", { mediaIds });
    return Boolean(response.data?.valid);
  },

  async getMediaSummaries(mediaIds: number[]): Promise<MediaSummary[]> {
    const response = await client.post("/v1/api/media/internal/summaries", { mediaIds });
    return response.data;
  },

  async registerMediaUsage(mediaIds: number[], usageType: string, referenceId: string): Promise<void> {
    await client.post("/v1/api/media/internal/usage/register", { mediaIds, usageType, referenceId });
  },

  async unregisterMediaUsage(mediaIds: number[], usageType: string, referenceId: string): Promise<void> {
    await client.post("/v1/api/media/internal/usage/unregister", { mediaIds, usageType, referenceId });
  }
};
