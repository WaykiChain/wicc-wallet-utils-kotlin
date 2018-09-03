/*
 * Developed by Richard Chen on 9/1/18 12:37 PM
 * Last modified 8/31/18 3:28 PM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet

import org.bitcoinj.params.TestNet3Params

/**
 * @Author: Richard Chen
 * @Date 2018/08/31 下午3:00
 */

class WaykiTestNetParams: TestNet3Params() {

    init {
        this.packetMagic = 0xd75c7dfd
        this.addressHeader = 135
        this.dumpedPrivateKeyHeader = 210
    }

    private object Holder { val INSTANCE = WaykiTestNetParams() }

    companion object {
        val instance: WaykiTestNetParams by lazy { Holder.INSTANCE }
    }
}