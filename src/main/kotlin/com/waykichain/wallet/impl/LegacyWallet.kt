/*
 * Developed by Richard Chen on 8/31/18 7:46 PM
 * Last modified 8/31/18 7:46 PM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet.impl

import com.waykichain.wallet.*
import com.waykichain.wallet.base.*
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.LegacyAddress

class LegacyWallet: LegacyWalletInterface {

    override fun generateWalletAddress(networkType: WaykiNetworkType): WalletAddress {
        val params = if (networkType == WaykiNetworkType.MAIN_NET) WaykiMainNetParams.instance
            else WaykiTestNetParams.instance
        val ecKey = ECKey()
        val privKey = ecKey.getPrivateKeyAsWiF(params)
        val pubKeyHash = ecKey.pubKeyHash
        val address = LegacyAddress.fromPubKeyHash(params, pubKeyHash).toString()

        val walletAddress = WalletAddress(ecKey, privKey, address)

        return walletAddress
    }

    override fun createRegisterTransaction(params: WaykiRegisterAccountTxParams, key: ECKey): String {
        params.pubKey = key.publicKeyAsHex
        return  params.serializeTx()
    }

    override fun createCommonTransaction(params: WaykiCommonTxParams, key: ECKey): String {
        params.pubKey = key.publicKeyAsHex
        return  params.serializeTx()
    }

}