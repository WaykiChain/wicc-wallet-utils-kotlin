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

package com.waykichain.wallet.impl

import com.waykichain.wallet.LegacyWalletInterface
import com.waykichain.wallet.base.WalletAddress
import com.waykichain.wallet.base.WaykiNetworkType
import com.waykichain.wallet.base.params.*
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.LegacyAddress

class LegacyWallet: LegacyWalletInterface {

    override fun generateWalletAddress(networkType: WaykiNetworkType): WalletAddress {
        val params = if (networkType == WaykiNetworkType.MAIN_NET) WaykiMainNetParams.instance else WaykiTestNetParams.instance
        val ecKey = ECKey()
        val privKeyWiF = ecKey.getPrivateKeyAsWiF(params)
        val pubKeyHash = ecKey.pubKeyHash
        val address = LegacyAddress.fromPubKeyHash(params, pubKeyHash).toString()

        val walletAddress = WalletAddress(ecKey, privKeyWiF, address)
        return walletAddress
    }

    override fun createRegisterTransactionRaw(params: WaykiRegisterAccountTxParams): String {
        return  params.serializeTx()
    }

    override fun createCommonTransactionRaw(params: WaykiCommonTxParams): String {
        return  params.serializeTx()
    }

    override fun createUCoinTransactionRaw(params: WaykiUCoinTxParams): String {
        return  params.serializeTx()
    }

    override fun createContractTransactionRaw(params: WaykiContractTxParams): String {
        return  params.serializeTx()
    }

    override fun createDelegateTransactionRaw(params: WaykiDelegateTxParams): String {
        return  params.serializeTx()
    }

    override fun createCdpStakeTransactionRaw(params: WaykiCdpStakeTxParams): String {
        return  params.serializeTx()
    }

    override fun createCdpRedeemTransactionRaw(params: WaykiCdpRedeemTxParams): String {
        return  params.serializeTx()
    }

    override fun createCdpLiquidateTransactionRaw(params: WaykiCdpLiquidateTxParams): String {
        return  params.serializeTx()
    }
    override fun createDexLimitTransactionRaw(params: WaykiDexLimitTxParams): String {
        return  params.serializeTx()
    }

    override fun createDexMarketTransactionRaw(params: WaykiDexMarketTxParams): String {
        return  params.serializeTx()
    }

    override fun createDexCancelOrderTransactionRaw(params: WaykiDexCancelOrderTxParams): String {
        return  params.serializeTx()
    }
}