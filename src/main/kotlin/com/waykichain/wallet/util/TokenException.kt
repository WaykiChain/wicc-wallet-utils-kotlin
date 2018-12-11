package org.waykichain.wallet.util

class TokenException : RuntimeException {
    companion object {
        internal var serialVersionUID = 4300404932829403534L
    }
    constructor(message: String?) : super(message)
    constructor(message: String?, e: Exception?) : super(message, e)
}