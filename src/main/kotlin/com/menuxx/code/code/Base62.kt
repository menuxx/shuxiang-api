package com.menuxx.code.code

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/25
 * 微信: yin80871901
 */
class Base62() {

    /**
     * Constructs a Base62 object with the default charset (0..9a..zA..Z).
     */
    private var characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    /**
     * Constructs a Base62 object with a custom charset.
     *
     * @param characters
     * the charset to use. Must be 62 characters.
     * @throws `IllegalArgumentException` if the supplied charset is not 62 characters long.
    `` */
    constructor(characters: String) : this() {
        if ( characters.length != 62 ) {
            throw IllegalArgumentException("Invalid string length, must be 62.")
        }
        this.characters = characters
    }

    /**
     * Encodes a decimal value to a Base62 `String`.
     *
     * @param b10
     * the decimal value to encode, must be nonnegative.
     * @return the number encoded as a Base62 `String`.
     */
    fun encodeBase10(b10: Long): String {
        var _b10 = b10
        if (_b10 < 0) {
            throw IllegalArgumentException("b10 must be nonnegative")
        }
        var ret = ""
        while (_b10 > 0) {
            ret = characters[(_b10 % 62).toInt()] + ret
            _b10 /= 62
        }
        return ret

    }

    /**
     * Decodes a Base62 `String` returning a `long`.
     *
     * @param b62
     * the Base62 `String` to decode.
     * @return the decoded number as a `long`.
     * @throws IllegalArgumentException
     * if the given `String` contains characters not
     * specified in the constructor.
     */
    fun decodeBase62(b62: String): Long {
        var _b62 = b62
        for (character in _b62.toCharArray()) {
            if ( !characters.contains(character.toString()) ) {
                throw IllegalArgumentException("Invalid character(s) in string: " + character)
            }
        }
        var ret: Long = 0
        _b62 = StringBuffer(_b62).reverse().toString()
        var count: Long = 1
        for (character in _b62.toCharArray()) {
            ret += characters.indexOf(character) * count
            count *= 62
        }
        return ret
    }

}