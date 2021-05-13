package com.example.rvec;

public class Main {
    public static void main(String[] args) {
//        var rv = RVec.fill(10, 10, RVec.Fraction.of(3,7));
//        var rv2 = RVec.fill(10, 10, RVec.Fraction.of(2,11));
//        var rv = RVec.rand(5,5).hstack(RVec.ones(5,1));
//        var rv2 = RVec.rand(5,5);

        var rv = RVec.rowBuilder(6)
                .addRow(0, 0, -2, 0, 7, 12)
                .addRow(2, 4, -10, 6, 12, 28)
                .addRow(2, 4, -5, 6, -5, -1)
                .toVec();

        rv = RVec.rowBuilder(3).addRow(7,4,-2).addRow(3,8,6).addRow(10,12,5).toVec();
        rv = RVec.rowBuilder(3).addRow(2,2,-2).addRow(-2,1,-7).addRow(-2,-1,-1).toVec();

        RVec.inspector = (op, vec) -> {
            System.out.println(op);
            System.out.println(vec);
        };

        RVec.inspector = null;

        System.out.println(rv);
//        var e = rv.gaussJordan();
//        e.getOps().forEach(System.out::println);
//        System.out.println(rv.rref());

        System.out.println("det(A) = " + rv.det());
    }
}
