package rng;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;

public class Xoroshiro128PlusPlus {

    private static final HashFunction MD5 = Hashing.md5();
    private static final long SILVER_RATIO_64 = 0x6A09E667F3BCC909L;
    private static final long GOLDEN_RATIO_64 = -7046029254386353131L;

    private long seedLo;
    private long seedHi;

    private final long seedLoHash;
    private final long seedHiHash;

    public Xoroshiro128PlusPlus(String identifier) {
        byte[] bs = MD5.hashString(identifier, Charsets.UTF_8).asBytes();
        seedLoHash = Longs.fromBytes(bs[0], bs[1], bs[2], bs[3], bs[4], bs[5], bs[6], bs[7]);
        seedHiHash = Longs.fromBytes(bs[8], bs[9], bs[10], bs[11], bs[12], bs[13], bs[14], bs[15]);
    }

    public void setSeed(long seed) {
        long l2 = seed ^ SILVER_RATIO_64;
        long l3 = l2 + GOLDEN_RATIO_64;
        this.seedLo = mixStafford13(l2 ^ seedLoHash);
        this.seedHi = mixStafford13(l3 ^ seedHiHash);

        if ((this.seedLo | this.seedHi) == 0L) {
            this.seedLo = GOLDEN_RATIO_64;
            this.seedHi = 7640891576956012809L;
        }
    }

    public static long mixStafford13(long seed) {
        seed = (seed ^ seed >>> 30) * -4658895280553007687L;
        seed = (seed ^ seed >>> 27) * -7723592293110705685L;
        return seed ^ seed >>> 31;
    }

    public long nextLong() {
        long l = this.seedLo;
        long m = this.seedHi;
        long n = Long.rotateLeft(l + m, 17) + l;
        m ^= l;
        this.seedLo = Long.rotateLeft(l, 49) ^ m ^ m << 21;
        this.seedHi = Long.rotateLeft(m, 28);
        return n;
    }

    private long nextBits(int i) {
        return this.nextLong() >>> (64 - i);
    }

    public float nextFloat() {
        return (float) this.nextBits(24) * 5.9604645E-8F;
    }

    public int nextInt() {
        return (int) this.nextLong();
    }

    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        long l = Integer.toUnsignedLong(this.nextInt());
        long m = l * (long) bound;
        long n = m & 4294967295L;
        if (n < (long) bound) {
            for (int j = Integer.remainderUnsigned(~bound + 1, bound); n < (long) j; n = m & 4294967295L) {
                l = Integer.toUnsignedLong(this.nextInt());
                m = l * (long) bound;
            }
        }
        return (int) (m >> 32);
    }

    public int nextInt(int min, int max) {
        if (min >= max) return min;
        return this.nextInt(max - min + 1) + min;
    }
}
