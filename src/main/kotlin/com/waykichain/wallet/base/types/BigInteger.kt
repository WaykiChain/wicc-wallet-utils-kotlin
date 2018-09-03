/*
 * Developed by Richard Chen on 9/1/18 8:24 AM
 * Last modified 9/1/18 8:24 AM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet.base.types

import java.math.BigInteger

infix fun BigInteger.ushr(bitCount: Int): BigInteger = shiftRight(bitCount)
