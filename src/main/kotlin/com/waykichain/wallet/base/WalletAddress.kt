package com.waykichain.wallet.base

import org.bitcoinj.core.ECKey

/**
 * priKey: privateKeyAsWiF
 * pubKey: publicKeyAsHex
 * address: WaykiChain wallet address
 *
 */
data class WalletAddress(val key: ECKey,
                         val privKey: String,
                         val address: String)