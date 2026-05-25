import { contractApi } from '../api/contractApi'
import { getApiErrorMessage } from '../utils/apiError'

type ApiResponse<T> = {
  success: boolean
  message: string
  data: T
}

export type ContractSummary = {
  id: string
  contractType: string
  title: string
  version: string
  isRequired: boolean
  effectiveDate: string
}

export type ContractDetail = ContractSummary & {
  content?: string
  isActive?: boolean
  createdAt?: string
  updatedAt?: string
}

export type AcceptContractsRequest = {
  userId: string
  acceptedContractIds: string[]
}

export const contractService = {
  getActiveContracts: async () => {
    try {
      const response = await contractApi.getActiveContracts()
      const payload = response.data as ApiResponse<ContractSummary[]>
      return payload.data ?? []
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
  getRequiredContracts: async () => {
    try {
      const response = await contractApi.getRequiredContracts()
      const payload = response.data as ApiResponse<ContractSummary[]>
      return payload.data ?? []
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
  getContractDetail: async (contractId: string) => {
    try {
      const response = await contractApi.getContractDetail(contractId)
      const payload = response.data as ApiResponse<ContractDetail>
      return payload.data
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
  validateRequiredContracts: async (acceptedContractIds: string[]) => {
    try {
      const response = await contractApi.validateRequiredContracts({
        acceptedContractIds,
      })
      return response.data as ApiResponse<{
        valid: boolean
        missingRequiredContracts: ContractSummary[]
        message: string
      }>
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
  acceptContracts: async (payload: AcceptContractsRequest) => {
    try {
      await contractApi.acceptContracts(payload)
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
}
