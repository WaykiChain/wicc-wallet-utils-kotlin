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

package com.waykichain.wallet.base

enum class WaykiTxType(val value: Int) {
    TX_NONE                 (0),
    TX_REGISTERACCOUNT      (2),
    TX_COMMON               (3),
    TX_CONTRACT             (4),
    TX_DELEGATE             (6)
}

enum class VoteOperType(val value: Int) {
    NULL_OPER (0),			//
    ADD_FUND  (1), 		//投票
    MINUS_FUND  (2), 	//撤销投票
}