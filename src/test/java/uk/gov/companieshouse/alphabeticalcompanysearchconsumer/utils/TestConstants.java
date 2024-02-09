package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils;

import static java.util.Collections.emptyList;

import uk.gov.companieshouse.stream.EventRecord;
import uk.gov.companieshouse.stream.ResourceChangedData;

public class TestConstants {

    private TestConstants() {}

    // TODO BI-13524 Come up with a better update?
    public static final ResourceChangedData UPDATE =
        ResourceChangedData.newBuilder()
            .setResourceKind("company-profile")
            .setResourceUri("/company/00006400")
            .setContextId("22-usZuMZEnZY6W_Kip1539964678")
            .setResourceId("???")
            .setData("{}")
            .setEvent(getEvent())
            .build();

    private static EventRecord getEvent() {
        return EventRecord.newBuilder()
            .setPublishedAt("1453896193333")
            .setType("changed")
            .setFieldsChanged(emptyList())
            .build();
    }
}
