package util;

public class ArrayOperation {

    public static double findMaxInArray(double[] values){
        double res = -Double.MAX_VALUE;

        if (values == null){
            return res;
        }

        if (values.length == 1){
            return values[0];
        }

        for (double tmp: values){
            if (tmp > res){
                res = tmp;
            }
        }

        return res;
    }

}
