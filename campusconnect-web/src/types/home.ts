export type UserSummary = {
  id?: string
  name?: string
  username?: string
  avatarUrl?: string
  role?: string
}

export type PostSummary = {
  id: string
  authorName: string
  authorUsername: string
  createdAt: string
  content: string
  mediaUrls?: string[]
  likeCount?: number
  commentCount?: number
}

export type EventSummary = {
  id: string
  title: string
  startsAt: string
  location?: string
}

export type FriendSuggestion = {
  id: string
  name: string
  username: string
  mutualCount?: number
}

export type ClubSummary = {
  id: string
  name: string
  description?: string
}
