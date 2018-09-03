/*
 * Developed by Richard Chen on 9/1/18 7:53 AM
 * Last modified 9/1/18 7:53 AM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet.base

import org.bitcoinj.core.ECKey

abstract class BaseSignTxParams {

    abstract fun serializeTx(): String
    abstract fun getSignatureHash():  ByteArray
    abstract fun signTx(key: ECKey): ByteArray

    var password = ""
    var nTxType = 0
    var nVersion = 0
    var nValidHeight = 0
    var fees = 10000 // 0.0001 wicc
    var signature: ByteArray? = null

}