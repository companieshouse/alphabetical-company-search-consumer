package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.stream.ResourceChangedData;

@ExtendWith(MockitoExtension.class)
class ServiceParametersTest {

    @Mock
    private ResourceChangedData resourceChangedData;

    @Mock
    private ResourceChangedData otherResourceChangedData;

    @Test
    void shouldReturnData() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(resourceChangedData);

        // Act & Assert
        assertEquals(resourceChangedData, serviceParameters.getData());
    }

    @Test
    void shouldReturnTrueWhenComparingSameInstance() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(resourceChangedData);

        // Act & Assert
        assertEquals(serviceParameters, serviceParameters);
    }

    @Test
    void shouldReturnTrueWhenComparingEqualInstances() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(resourceChangedData);
        ServiceParameters otherServiceParameters = new ServiceParameters(resourceChangedData);

        // Act & Assert
        assertEquals(serviceParameters, otherServiceParameters);
    }

    @Test
    void shouldReturnFalseWhenComparingDifferentData() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(resourceChangedData);
        ServiceParameters otherServiceParameters = new ServiceParameters(otherResourceChangedData);

        // Act & Assert
        assertNotEquals(serviceParameters, otherServiceParameters);
    }

    @Test
    void shouldReturnFalseWhenComparingWithNull() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(resourceChangedData);

        // Act & Assert
        assertNotEquals(null, serviceParameters);
    }

    @Test
    void shouldReturnFalseWhenComparingWithDifferentType() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(resourceChangedData);

        // Act & Assert
        assertNotEquals("not a ServiceParameters", serviceParameters);
    }

    @Test
    void shouldReturnSameHashCodeForEqualInstances() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(resourceChangedData);
        ServiceParameters otherServiceParameters = new ServiceParameters(resourceChangedData);

        // Act & Assert
        assertEquals(serviceParameters.hashCode(), otherServiceParameters.hashCode());
    }

    @Test
    void shouldReturnDifferentHashCodeForDifferentData() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(resourceChangedData);
        ServiceParameters otherServiceParameters = new ServiceParameters(otherResourceChangedData);

        // Act & Assert
        assertNotEquals(serviceParameters.hashCode(), otherServiceParameters.hashCode());
    }

    @Test
    void shouldReturnNonNullHashCode() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(resourceChangedData);

        // Act & Assert
        assertNotNull(serviceParameters.hashCode());
    }

    @Test
    void shouldHandleNullData() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(null);

        // Act & Assert
        assertNull(serviceParameters.getData());
    }

    @Test
    void shouldReturnTrueWhenBothDataAreNull() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(null);
        ServiceParameters otherServiceParameters = new ServiceParameters(null);

        // Act & Assert
        assertEquals(serviceParameters, otherServiceParameters);
    }

    @Test
    void shouldReturnFalseWhenOneDataIsNull() {
        // Arrange
        ServiceParameters serviceParameters = new ServiceParameters(null);
        ServiceParameters otherServiceParameters = new ServiceParameters(resourceChangedData);

        // Act & Assert
        assertNotEquals(serviceParameters, otherServiceParameters);
    }
}