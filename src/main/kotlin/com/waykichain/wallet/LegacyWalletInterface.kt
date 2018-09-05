package com.waykichain.wallet

import com.waykichain.wallet.base.WalletAddress
import org.bitcoinj.core.ECKey

interface LegacyWalletInterface {

    /**
     * creation of wallet priv/pub key pair and corresponding address
     */
    fun generateWalletAddress(networkType: WaykiNetworkType): WalletAddress

    /**
     * offline creation of Register Account Transaction raw data
     */
    fun createRegisterTransactionRaw(params: WaykiRegisterAccountTxParams, key: ECKey): String

    /**
     * offline creation of Common Transaction raw data
     */
    fun createCommonTransactionRaw(params: WaykiCommonTxParams, key: ECKey): String

}