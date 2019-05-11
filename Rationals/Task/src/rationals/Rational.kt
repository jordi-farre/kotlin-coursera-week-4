package rationals

import rationals.Rational.Companion.from
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

infix fun Int.divBy(denominator: Int): Rational = from(this.toBigInteger(), denominator.toBigInteger())
infix fun Long.divBy(denominator: Long): Rational = from(this.toBigInteger(), denominator.toBigInteger())
infix fun BigInteger.divBy(denominator: BigInteger): Rational = from(this, denominator)
fun BigInteger.lcm(other: BigInteger): BigInteger = this * (other / this.gcd(other))

fun String.toRational(): Rational {
    val splittedValues = this.split("/")
    if (splittedValues.size == 1) return from(splittedValues[0].toBigInteger(), ONE)
    return from(splittedValues[0].toBigInteger(), splittedValues[1].toBigInteger())
}

class RationalRange(override val start: Rational, override val endInclusive: Rational): ClosedRange<Rational>

data class Rational private constructor(val numerator: BigInteger, val denominator: BigInteger): Comparable<Rational> {

    operator fun plus(rational: Rational): Rational =
            from(this.numerator * rational.denominator + rational.numerator * this.denominator, this.denominator * rational.denominator)


    operator fun minus(rational: Rational): Rational =
            from(this.numerator * rational.denominator - rational.numerator * this.denominator, this.denominator * rational.denominator)

    operator fun times(rational: Rational): Rational =
            from(this.numerator * rational.numerator, this.denominator * rational.denominator)

    operator fun div(rational: Rational): Rational =
            from(this.numerator * rational.denominator, this.denominator * rational.numerator)

    operator fun unaryMinus(): Rational = Rational(this.numerator * (-1).toBigInteger(), this.denominator)

    override operator fun compareTo(rational: Rational): Int {
        val lcm = this.denominator.lcm(rational.denominator)
        val rational1 = Rational(this.numerator * (lcm / this.denominator), this.denominator * (lcm / this.denominator))
        val rational2 = Rational(rational.numerator * (lcm / rational.denominator), rational.denominator * (lcm / rational.denominator))
        return rational1.numerator.compareTo(rational2.numerator)
    }

    operator fun rangeTo(rational: Rational): RationalRange = RationalRange(this, rational)

    override fun toString(): String =
        if (denominator == ONE) numerator.toString()
        else """${numerator}/${denominator}"""

    private fun normalize(): Rational {
        val commonDenominator = this.numerator.gcd(this.denominator)
        return if (this.denominator < ZERO) Rational(this.numerator.negate() / commonDenominator, this.denominator.negate() / commonDenominator)
        else Rational(this.numerator / commonDenominator, this.denominator / commonDenominator)
    }

    companion object {
        fun from(numerator: BigInteger, denominator: BigInteger): Rational =
                Rational(numerator, denominator).normalize()
    }
}

fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}