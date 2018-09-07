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

package com.waykichain.wallet.base.params

import com.waykichain.wallet.base.WaykiTxType
import org.bitcoinj.core.ECKey

abstract class BaseSignTxParams {

    abstract fun serializeTx(): String
    abstract fun getSignatureHash():  ByteArray
    abstract fun signTx(key: ECKey): ByteArray

    var nTxType = WaykiTxType.TX_NONE
    var nVersion = 0L
    var nValidHeight = 0L
    var fees = 10000L // 0.0001 wicc
    var signature: ByteArray? = null

    var password = ""
    var userPubKey  = ByteArray(33)
    var minerPubKey: ByteArray? = null

}