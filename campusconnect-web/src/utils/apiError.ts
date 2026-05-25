import axios from 'axios'

type ErrorResponse = {
  message?: string
  errors?: Record<string, string>
}

const NETWORK_ERROR_MESSAGE =
  'Sunucuya baglanilamadi. API Gateway calisiyor mu kontrol edin.'

export const getApiErrorMessage = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    if (error.code === 'ERR_NETWORK') {
      return NETWORK_ERROR_MESSAGE
    }

    const data = error.response?.data as ErrorResponse | undefined
    if (data?.message) {
      return data.message
    }

    const firstFieldError = data?.errors
      ? Object.values(data.errors)[0]
      : undefined

    if (firstFieldError) {
      return firstFieldError
    }

    return 'Bir hata olustu. Lutfen tekrar deneyin.'
  }

  return 'Bir hata olustu. Lutfen tekrar deneyin.'
}
