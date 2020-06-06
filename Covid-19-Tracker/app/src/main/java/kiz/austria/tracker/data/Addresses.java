package kiz.austria.tracker.data;

public class Addresses {

    public static class Link {

        private static final String HTTP_REQUEST = "https://coronavirus-19-api.herokuapp.com/";
        public static final String DATA_WORLD = HTTP_REQUEST + "countries/World";
        public static final String DATA_PHILIPPINES = HTTP_REQUEST + "countries/Philippines";
        public static final String DATA_PHILIPPINES_FROM_APIFY = "https://api.apify.com/v2/datasets/sFSef5gfYg3soj8mb/items?format=json&clean=1";
        public static final String DATA_PH_DROP_CASES = "https://coronavirus-ph-api.herokuapp.com/cases";
        public static final String DATA_PH_DROP_DOH = "https://coronavirus-ph-api.herokuapp.com/doh-data-drop";
        public static final String DATA_COUNTRIES = HTTP_REQUEST + "countries";

//        public static String DATA_COUNTRY;
//
//        public static void buildCountryLink(String link, String postfix) {
//            DATA_COUNTRY = link + postfix;
//        }

        private Link() {
            //empty constructor
        }

    }

    private Addresses() {
        //empty constructor
    }

}
