package kiz.austria.tracker.data;

public class Addresses {

    public static class Link {

        public static final String DATA_GLOBAL = "https://coronavirus-19-api.herokuapp.com/all";
        public static final String DATA_COUNTRIES = "https://coronavirus-19-api.herokuapp.com/countries";
        public static String DATA_COUNTRY;
        public static void buildLink(String link, String postfix) {
            DATA_COUNTRY = link + postfix;
        }

        private Link() {
            //empty constructor
        }

    }

    private Addresses() {
        //empty constructor
    }

}
