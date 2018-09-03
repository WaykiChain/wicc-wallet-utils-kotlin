/*
 * Developed by Richard Chen on 9/1/18 12:37 PM
 * Last modified 8/31/18 3:26 PM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet

import org.bitcoinj.params.MainNetParams

/**
 * @Author: Richard Chen
 * @Date 2018/08/31 下午3:00
 */

class WaykiMainNetParams: MainNetParams() {

    init {
        this.packetMagic = 0x1a1d42ff
        this.addressHeader = 73
        this.dumpedPrivateKeyHeader = 153
    }

    private object Holder { val INSTANCE = WaykiMainNetParams() }

    companion object {
        val instance: WaykiMainNetParams by lazy { Holder.INSTANCE }
    }
}