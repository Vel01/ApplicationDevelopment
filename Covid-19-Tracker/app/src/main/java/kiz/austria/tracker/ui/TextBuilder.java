package kiz.austria.tracker.ui;

public interface TextBuilder {
    String textBuilder(String gender, String age);

    String textBuilder(String admitted);

    void formatDate(String reported);

    void statusBuilder(String recovered, String died);

    void inconsistentDataBuilder(String province, String location, String latitude, String longitude);
}
