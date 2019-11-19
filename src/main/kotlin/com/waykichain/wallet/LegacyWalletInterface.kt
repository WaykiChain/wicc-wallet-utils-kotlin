/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 The Waykichain Core developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package com.waykichain.wallet

import com.waykichain.wallet.base.WalletAddress
import com.waykichain.wallet.base.WaykiNetworkType
import com.waykichain.wallet.base.params.*
import org.bitcoinj.core.NetworkParameters

interface LegacyWalletInterface {

    /**
     * creation of wallet priv/pub key pair and corresponding address
     */
    fun generateWalletAddress(networkType: WaykiNetworkType): WalletAddress

    /**
     * offline creation of Register Account Transaction raw data
     */
    fun createRegisterTransactionRaw(params: WaykiRegisterAccountTxParams): String

    /**
     * offline creation of Common Transaction raw data
     */
    fun createCommonTransactionRaw(params: WaykiCommonTxParams): String

    /**
     * offline creation of Contract Transaction raw data
     */
    fun createContractTransactionRaw(params: WaykiContractTxParams): String

    /**
     * offline creation of Delegate Transaction raw data
     */
    fun createDelegateTransactionRaw(params: WaykiDelegateTxParams): String

    /**
     * offline creation of Cdp Stake Transaction raw data
     */
    fun createCdpStakeTransactionRaw(params: WaykiCdpStakeTxParams): String

    /**
     * offline creation of Cdp Redeem Transaction raw data
     */
    fun createCdpRedeemTransactionRaw(params: WaykiCdpRedeemTxParams): String

    /**
     * offline creation of Cdp Liquidate Transaction raw data
     */
     fun createCdpLiquidateTransactionRaw(params: WaykiCdpLiquidateTxParams): String {
        return  params.serializeTx()
    }

    /**
     * offline creation of UCoin Transaction raw data
     */
    fun createUCoinTransactionRaw(params: WaykiUCoinTxParams): String {
        return  params.serializeTx()
    }

    /**
     * offline creation of Dex limit Transaction raw data
     */
    fun createDexLimitTransactionRaw(params: WaykiDexLimitTxParams): String {
        return  params.serializeTx()
    }

    /**
     * offline creation of Dex market Transaction raw data
     */
     fun createDexMarketTransactionRaw(params: WaykiDexMarketTxParams): String {
        return  params.serializeTx()
    }

    /**
     * offline creation of Dex cancel order Transaction raw data
     */
     fun createDexCancelOrderTransactionRaw(params: WaykiDexCancelOrderTxParams): String {
        return  params.serializeTx()
    }

    /**
     * offline creation of UCoin contract Invoke
     */
     fun createUCoinContractInvokeRaw(params: WaykiUCoinContractTxParams): String {
        return  params.serializeTx()
    }

    /*
    *
    * */
     fun createAssetIssueRaw(params: WaykiAssetIssueTxParams): String {
        return  params.serializeTx()
    }

    fun createAssetUpdateRaw(params: WaykiAssetUpdateTxParams): String {
        return  params.serializeTx()
    }

    fun createDeployContractRaw(params: WaykiDeployContractTxParams): String {
        return  params.serializeTx()
    }

    fun createSignMessage(params: WaykiSignMsgParams): WaykiSignMsgParams.SignResult {
        return  params.serializeSignature()
    }

    fun verifyMsgSignature(params: WaykiVerifyMsgSignParams): WaykiVerifyMsgSignParams.VerifyMsgSignatureResult {
        return  params.verifyMsgSignature()
    }

    fun parseUCoinTransactionRaw(params: String, net: NetworkParameters): BaseSignTxParams{
        return WaykiUCoinTxParams.unSerializeTx(params, net)
    }
}