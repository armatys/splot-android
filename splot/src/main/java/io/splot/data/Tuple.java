package io.splot.data;

/** Represents an ordered list of elements.
 * Created by mako on 25.02.2015.
 */
public class Tuple {
    public static <T1, T2> Tuple2 from(T1 first, T2 second) {
        return new Tuple2<T1, T2>(first, second);
    }

    public static <T1, T2, T3> Tuple3 from(T1 first, T2 second, T3 third) {
        return new Tuple3<T1, T2, T3>(first, second, third);
    }

    public static <T1, T2, T3, T4> Tuple4 from(T1 first, T2 second, T3 third, T4 fourth) {
        return new Tuple4<T1, T2, T3, T4>(first, second, third, fourth);
    }
    
    public static class Tuple2<T1, T2> {
        protected T1 mFirst;
        protected T2 mSecond;

        Tuple2(T1 first, T2 second) {
            mFirst = first;
            mSecond = second;
        }
        
        public T1 getFirst() {
            return mFirst;
        }
        
        public T2 getSecond() {
            return mSecond;
        }

        @Override public String toString() {
            return "Tuple2(" + String.valueOf(mFirst) + ", " + String.valueOf(mSecond) + ")";
        }
    }
    
    public static class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {
        protected T3 mThird;

        public Tuple3(T1 first, T2 second, T3 third) {
            super(first, second);
            mThird = third;
        }
        
        public T3 getThird() {
            return mThird;
        }

        @Override public String toString() {
            return "Tuple3(" + String.valueOf(mFirst) + ", " + String.valueOf(mSecond) + ", " + String.valueOf(mThird) + ")";
        }
    }
    
    public static class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {
        protected T4 mFourth;

        public Tuple4(T1 first, T2 second, T3 third, T4 fourth) {
            super(first, second, third);
            mFourth = fourth;
        }
        
        public T4 getFourth() {
            return mFourth;
        }

        @Override public String toString() {
            return "Tuple4(" + String.valueOf(mFirst) + ", " + String.valueOf(mSecond) + "," + String.valueOf(mThird) + ", " + String.valueOf(mFourth) + ")";
        }
    }
}
