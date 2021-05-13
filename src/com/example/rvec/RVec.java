package com.example.rvec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

@SuppressWarnings("unused")
public class RVec {
    private final Fraction[][] ma;
    private final int m;
    private final int n;


    private RVec(int m, int n, Fraction[][] ma) {
        if (m < 1 || n < 1) {
            throw new IllegalArgumentException();
        }
        if (ma.length != m || ma[0].length != n) {
            throw new IllegalArgumentException();
        }
        this.m = m;
        this.n = n;
        this.ma = ma;
    }

    private RVec(int m, int n) {
        this(m, n, new Fraction[m][n]);
    }

    public static RVec fill(int m, int n, Fraction f) {
        var rv = new RVec(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rv.ma[i][j] = f;
            }
        }
        return rv;
    }

    public static RVec zeros(int m, int n) {
        return fill(m, n, Fraction.ZERO);
    }

    public static RVec ones(int m, int n) {
        return fill(m, n, Fraction.ONE);
    }

    public static RVec eye(int n) {
        var rv = zeros(n, n);
        for (int i = 0; i < n; i++) {
            rv.ma[i][i] = Fraction.ONE;
        }
        return rv;
    }

    public static RVec rand(int m, int n) {
        var rv = new RVec(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rv.ma[i][j] = Fraction
                        .fromLong((long) (Math.random() * 99) + 1);
            }
        }
        return rv;
    }

    public static RowBuilder rowBuilder(int cols) {
        return new RowBuilder(cols);
    }

    public static RVec vector(long... e) {
        var rv = new RVec(1, e.length);
        for (int i = 0; i < e.length; i++) {
            rv.ma[i][0] = Fraction.fromLong(e[i]);
        }
        return rv;
    }

    public Fraction get(int i, int j) {
        return ma[i][j];
    }

    public RVec transpose() {
        var rv = new RVec(n, m);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rv.ma[j][i] = ma[i][j];
            }
        }
        return rv;
    }

    public Fraction trace() {
        var s = Fraction.ZERO;
        var nd = Math.min(m, n);
        for (int i = 0; i < nd; i++) {
            s = s.add(ma[i][i]);
        }
        return s;
    }

    public RVec add(RVec other) {
        if (m != other.m || n != other.n) {
            throw new ArithmeticException("addition is undefined");
        }
        var rv = new RVec(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rv.ma[i][j] = ma[i][j].add(other.ma[i][j]);
            }
        }
        return rv;
    }

    public RVec mul(Fraction k) {
        var rv = new RVec(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rv.ma[i][j] = ma[i][j].mul(k);
            }
        }
        return rv;
    }

    public RVec neg() {
        var rv = new RVec(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rv.ma[i][j] = ma[i][j].neg();
            }
        }
        return rv;
    }

    public RVec copy() {
        var rv = new RVec(m, n);
        for (int i = 0; i < m; i++) {
            System.arraycopy(ma[i], 0, rv.ma[i], 0, n);
        }
        return rv;
    }


    public boolean isZero() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (!ma[i][j].isZero()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isEye() {
        if (m != n) return false;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    if (!ma[i][j].isOne()) return false;
                } else {
                    if (!ma[i][j].isZero()) return false;
                }
            }
        }
        return true;
    }

    public Fraction getValue() {
        if (m > 1 || n > 1) {
            throw new IllegalStateException();
        }
        return ma[0][0];
    }

    public RVec dot(RVec other) {
        if (n != other.m) {
            throw new ArithmeticException();
        }
        var rv = new RVec(m, other.n);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < other.n; j++) {
                var s = Fraction.ZERO;
                for (int k = 0; k < n; k++) {
                    s = s.add(ma[i][k].mul(other.ma[k][j]));
                }
                rv.ma[i][j] = s;
            }
        }

        return rv;
    }

    public Fraction sum() {
        var s = Fraction.ZERO;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                s = s.add(ma[i][j]);
            }
        }
        return s;
    }

    public RVec lower() {
        var rv = new RVec(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i >= j) {
                    rv.ma[i][j] = ma[i][j];
                } else {
                    rv.ma[i][j] = Fraction.ZERO;
                }
            }
        }
        return rv;
    }

    public RVec upper() {
        var rv = new RVec(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (j >= i) {
                    rv.ma[i][j] = ma[i][j];
                } else {
                    rv.ma[i][j] = Fraction.ZERO;
                }
            }
        }
        return rv;
    }

    public RVec hstack(RVec... o) {
        int w = n;
        for (RVec vec : o) {
            if (vec.m != m) {
                throw new IllegalArgumentException();
            }
            w += vec.n;
        }
        var rv = new RVec(m, w);

        for (int i = 0; i < m; i++) {
            System.arraycopy(ma[i], 0, rv.ma[i], 0, n);
        }

        int c = n;
        for (RVec vec : o) {
            for (int i = 0; i < m; i++) {
                System.arraycopy(vec.ma[i], 0, rv.ma[i], c, vec.n);
            }
            c += vec.n;
        }

        return rv;
    }

    private static void gauss(RVec rv, Elimination elim) {
        int i = 0;
        int j = 0;

        while (i < rv.m && j < rv.n) {
            var pivot = rv.ma[i][j];
            if (pivot.isZero()) {
                int k = i + 1;
                boolean found = false;
                while (k < rv.m) {
                    if (!rv.ma[k][j].isZero()) {
                        found = true;
                        break;
                    }
                    k++;
                }
                if (!found) {
                    j += 1;
                    continue;
                }
                elim.exchange(i, k);
                pivot = rv.ma[i][j];
            }
            if (!pivot.isOne()) {
                elim.mul(i, pivot.recip());
            }
            for (int k = i + 1; k < rv.m; k++) {
                var ce = rv.ma[k][j];
                if (!ce.isZero()) {
                    elim.lc(k, i, ce.neg());
                }
            }
            i += 1;
            j += 1;
        }
    }

    private static void jordan(RVec rv, Elimination elim) {
        elim.zeroRows = 0;
        for (int i = rv.m - 1; i > 0; i--) {
            int j = 0;
            boolean found = false;
            while (j < rv.n) {
                if (!rv.ma[i][j].isZero()) {
                    found = true;
                    break;
                }
                j++;
            }
            if (!found) {
                elim.zeroRows++;
                continue;
            }
            for (int k = i - 1; k >= 0; k--) {
                var ce = rv.ma[k][j];
                if (!ce.isZero()) {
                    elim.lc(k, i, ce.neg());
                }
            }
        }
    }

    public Elimination gaussJordan() {
        var rv = copy();
        var elim = new Elimination(rv);
        gauss(rv, elim);
        jordan(rv, elim);
        return elim;
    }

    public RVec rref() {
        return gaussJordan().getVector();
    }

    public RVec inv() {
        if (m != n) {
            throw new UnsupportedOperationException();
        }
        var elim = gaussJordan();
        if (elim.zeroRows > 0) {
            throw new ArithmeticException("Matrix is not invertible");
        }
        var inverse = eye(n);
        for (EOp op : elim.getOps()) {
            op.transform(inverse);
        }
        return inverse;
    }

    public Fraction det() {
        if (m != n) {
            throw new UnsupportedOperationException();
        }
        var rv = copy();
        var elim = new Elimination(rv);
        gauss(rv, elim);

        for (int i = m - 1; i >= 0; i--) {
            if (rv.ma[i][i].isZero()) {
                return Fraction.ZERO;
            }
        }

        Fraction prod = Fraction.ONE;
        var ops = elim.getOps();
        for (int i = ops.size() - 1; i >= 0; i--) {
            var op = ops.get(i);
            if (op.isExchange()) prod = prod.neg();
            else if (op.isRowMul()) prod = prod.div(op.k);
        }

        return prod;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();
        return copy();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        sb.append(m).append(" Rows, ").append(n).append(" Columns]\n");

        var sfy = new String[m][n];
        var cols = new int[n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                var s = ma[i][j].toString();
                sfy[i][j] = s;
                cols[j] = Math.max(s.length(), cols[j]);
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                var s = sfy[i][j];
                var w = cols[j];
                sb.append(" ".repeat(w - s.length())).append(s);
                if (j != n - 1) {
                    sb.append("    ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private RVec compare(RVec o, BiPredicate<Fraction, Fraction> cond) {
        if (m != o.m || n != o.n) {
            throw new IllegalArgumentException();
        }
        var rv = new RVec(m, n);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rv.ma[i][j] = cond.test(ma[i][j], o.ma[i][j])
                        ? Fraction.ONE : Fraction.ZERO;
            }
        }
        return rv;
    }

    public RVec compareEquals(RVec o) {
        return compare(o, Fraction::equals);
    }

    public RVec compareNotEquals(RVec o) {
        return compare(o, Fraction::notEquals);
    }

    public RVec compareGreater(RVec o) {
        return compare(o, Fraction::isGreater);
    }

    public RVec compareGreaterEqual(RVec o) {
        return compare(o, Fraction::isGreaterEqual);
    }

    public RVec compareLesser(RVec o) {
        return compare(o, Fraction::isLesser);
    }

    public RVec compareLesserEqual(RVec o) {
        return compare(o, Fraction::isLesserEqual);
    }

    public boolean strictEquals(RVec o) {
        if (m != o.m || n != o.n) {
            return false;
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (!ma[i][j].equals(o.ma[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return strictEquals((RVec) o);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(m, n);
        result = 31 * result + Arrays.deepHashCode(ma);
        return result;
    }

    public static class EOp {
        final int row1;
        final int row2;
        final Fraction k;

        private EOp(int row1, int row2, Fraction k) {
            this.row1 = row1;
            this.row2 = row2;
            this.k = k;
        }

        boolean isExchange() {
            return k == null;
        }

        boolean isRowMul() {
            return row2 == -1;
        }

        boolean isLC() {
            return !isExchange() && !isRowMul();
        }

        EOp invert() {
            if (isExchange()) return this;
            if (isRowMul()) return new EOp(row1, -1, k.recip());
            return new EOp(row1, row2, k.neg());
        }

        public void transform(RVec rv) {
            var ma = rv.ma;
            if (isExchange()) {
                var tmp = ma[row1];
                ma[row1] = ma[row2];
                ma[row2] = tmp;
                return;
            }
            if (isRowMul()) {
                var row = ma[row1];
                for (int i = 0; i < row.length; i++) {
                    row[i] = row[i].mul(k);
                }
                return;
            }
            var r1 = ma[row1];
            var r2 = ma[row2];
            for (int i = 0; i < rv.n; i++) {
                r1[i] = r1[i].add(r2[i].mul(k));
            }
        }

        static void transform(RVec rv, List<EOp> eOps) {
            for (EOp eOp : eOps) {
                eOp.transform(rv);
            }
        }

        RVec asVec(int m) {
            var rv = eye(m);
            transform(rv);
            return rv;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EOp eOp = (EOp) o;
            return row1 == eOp.row1 && row2 == eOp.row2 && Objects.equals(k, eOp.k);
        }

        @Override
        public int hashCode() {
            return Objects.hash(row1, row2, k);
        }

        @Override
        public String toString() {
            var r1 = row1 + 1;
            var r2 = row2 + 1;

            if (isExchange()) {
                return "r" + r1 + " <-> r" + r2;
            }
            if (isRowMul()) {
                return "r" + r1 + " -> " + k + "*r" + r1;
            }
            return "r" + r1 + " -> r" + r1 + " + " + k + "*r" + r2;
        }
    }

    public static class RowBuilder {
        private final int cols;
        private final List<Fraction[]> rows = new ArrayList<>();

        private RowBuilder(int cols) {
            this.cols = cols;
        }

        public RowBuilder addRow(long... e) {
            if (e.length != cols) {
                throw new IllegalArgumentException();
            }
            var fs = new Fraction[cols];
            for (int i = 0; i < cols; i++) {
                fs[i] = Fraction.fromLong(e[i]);
            }
            rows.add(fs);
            return this;
        }

        public RVec toVec() {
            return new RVec(rows.size(), cols, rows.toArray(new Fraction[][]{}));
        }
    }

    public static class Fraction {
        private final long num;
        private final long den;

        private static long gcd(long a, long b) {
            return b == 0 ? a : gcd(b, a % b);
        }

        public static Fraction fromLong(long num) {
            return new Fraction(num, 1);
        }

        public static Fraction of(long num, long den) {
            if (den == 0) {
                throw new ArithmeticException();
            }
            if (den < 0) {
                den = -den;
                num = -num;
            }
            var gcd = gcd(Math.abs(num), den);
            num /= gcd;
            den /= gcd;

            return new Fraction(num, den);
        }

        private Fraction(long num, long den) {
            if (den <= 0) throw new IllegalStateException();
            this.num = num;
            this.den = den;
        }

        public double eval() {
            return ((double) num) / den;
        }

        public long getNum() {
            return num;
        }

        public long getDen() {
            return den;
        }

        public static final Fraction ONE = new Fraction(1, 1);
        public static final Fraction ZERO = new Fraction(0, 1);


        public Fraction add(Fraction other) {
            return Fraction.of(num * other.den + other.num * den, den * other.den);
        }

        public Fraction add(long other) {
            return Fraction.of(num + other * den, den);
        }

        public Fraction sub(Fraction other) {
            return Fraction.of(num * other.den - other.num * den, den * other.den);
        }

        public Fraction sub(long other) {
            return Fraction.of(num - other * den, den);
        }

        public Fraction mul(Fraction other) {
            return Fraction.of(num * other.num, den * other.den);
        }

        public Fraction mul(long other) {
            return Fraction.of(num * other, den);
        }

        public Fraction div(Fraction other) {
            return Fraction.of(num * other.den, den * other.num);
        }

        public Fraction div(long other) {
            return Fraction.of(num, den * other);
        }

        public Fraction recip() {
            return Fraction.of(den, num);
        }

        public Fraction neg() {
            return new Fraction(-num, den);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean isZero() {
            return num == 0;
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean isOne() {
            return num == den;
        }

        public boolean isGreater(Fraction o) {
            return eval() > o.eval();
        }

        public boolean isGreaterEqual(Fraction o) {
            return eval() >= o.eval();
        }

        public boolean isLesser(Fraction o) {
            return eval() < o.eval();
        }

        public boolean isLesserEqual(Fraction o) {
            return eval() <= o.eval();
        }

        @Override
        public String toString() {
            if (num == 0) {
                return "0";
            }
            if (den == 1) {
                return "" + num;
            }
            return num + "/" + den;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Fraction fraction = (Fraction) o;
            return num == fraction.num && den == fraction.den;
        }

        public boolean notEquals(Fraction o) {
            return !equals(o);
        }

        @Override
        public int hashCode() {
            return Float.floatToIntBits((float) num / den);
        }
    }

    private static class Pair<A, B> {
        final A a;
        final B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }
    }

    @FunctionalInterface
    public interface OpStateInspector {
        void call(EOp op, RVec vec);
    }

    public static OpStateInspector inspector;

    public static class Elimination {
        private final RVec rv;
        private final List<EOp> ops = new ArrayList<>();
        int zeroRows = 0;

        public Elimination(RVec rv) {
            this.rv = rv;
        }

        void rowOp(EOp op) {
            op.transform(rv);
            if (inspector != null) {
                inspector.call(op, rv);
            }
            ops.add(op);
        }

        void exchange(int row1, int row2) {
            rowOp(new EOp(row1, row2, null));
        }

        void mul(int row, Fraction k) {
            rowOp(new EOp(row, -1, k));
        }

        void lc(int row1, int row2, Fraction k) {
            rowOp(new EOp(row1, row2, k));
        }

        public List<EOp> getOps() {
            return ops;
        }

        public RVec getVector() {
            return rv;
        }
    }

    public static class SingularException extends ArithmeticException {
        public SingularException() {
        }

        public SingularException(String s) {
            super(s);
        }
    }

}
