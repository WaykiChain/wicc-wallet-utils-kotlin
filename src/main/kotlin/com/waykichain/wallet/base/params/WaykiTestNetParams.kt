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