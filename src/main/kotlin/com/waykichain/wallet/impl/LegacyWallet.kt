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

import com.waykichain.wallet.*
import com.waykichain.wallet.base.*
import com.waykichain.wallet.base.params.WaykiCommonTxParams
import com.waykichain.wallet.base.params.WaykiMainNetParams
import com.waykichain.wallet.base.params.WaykiRegisterAccountTxParams
import com.waykichain.wallet.base.params.WaykiTestNetParams
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.LegacyAddress

class LegacyWallet: LegacyWalletInterface {

    override fun generateWalletAddress(networkType: WaykiNetworkType): WalletAddress {
        val params = if (networkType == WaykiNetworkType.MAIN_NET) WaykiMainNetParams.instance else WaykiTestNetParams.instance
        val ecKey = ECKey()
        val privKey = ecKey.getPrivateKeyAsWiF(params)
        val pubKeyHash = ecKey.pubKeyHash
        val address = LegacyAddress.fromPubKeyHash(params, pubKeyHash).toString()
        val walletAddress = WalletAddress(ecKey, privKey, address)

        return walletAddress
    }

    override fun createRegisterTransactionRaw(params: WaykiRegisterAccountTxParams): String {
        return  params.serializeTx()
    }

    override fun createCommonTransactionRaw(params: WaykiCommonTxParams): String {
        return  params.serializeTx()
    }

}