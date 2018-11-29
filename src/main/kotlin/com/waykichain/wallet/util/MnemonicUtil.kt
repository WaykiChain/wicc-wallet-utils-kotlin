package org.waykichain.wallet.util

import org.bitcoinj.crypto.MnemonicCode

class MnemonicUtil {

    companion object {
        fun validateMnemonics(mnemonicCodes: List<String>) {
            try {
                MnemonicCode.INSTANCE.check(mnemonicCodes)
            } catch (e: org.bitcoinj.crypto.MnemonicException.MnemonicLengthException) {
                throw TokenException(Messages.MNEMONIC_INVALID_LENGTH)
            } catch (e: org.bitcoinj.crypto.MnemonicException.MnemonicWordException) {
                throw TokenException(Messages.MNEMONIC_BAD_WORD)
            } catch (e: Exception) {
                throw TokenException(Messages.MNEMONIC_CHECKSUM_CHECKSUM)
            }
        }

        fun randomMnemonicCodes(): List<String> {
            return toMnemonicCodes(NumericUtil.generateRandomBytes(16))
        }

        fun toMnemonicCodes(entropy: ByteArray): List<String> {
            try {
                return MnemonicCode.INSTANCE.toMnemonic(entropy)
            } catch (e: org.bitcoinj.crypto.MnemonicException.MnemonicLengthException) {
                throw TokenException(Messages.MNEMONIC_INVALID_LENGTH)
            } catch (e: Exception) {
                throw TokenException(Messages.MNEMONIC_CHECKSUM_CHECKSUM)
            }
        }
    }
}