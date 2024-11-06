package demo.results;

import demo.sample.HSClassFile;
import org.junit.jupiter.api.Test;

import java.lang.String;

import static org.junit.jupiter.api.Assertions.*;


public class CodeiumTests{

    @Test
    public void testBuilder() {
        HSClassFile.Builder builder = new HSClassFile.Builder();
        HSClassFile hsClassFile = builder.build();
        assertNotNull(hsClassFile);
    }

    @Test
    public void testBuilderWithNullName() {
        HSClassFile.Builder builder = new HSClassFile.Builder();
        builder.name(null);
        try {
            HSClassFile hsClassFile = builder.build();
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void testBuilderWithEmptyName() {
        HSClassFile.Builder builder = new HSClassFile.Builder();
        builder.name("");
        try {
            HSClassFile hsClassFile = builder.build();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testBuilderWithValidName() {
        HSClassFile.Builder builder = new HSClassFile.Builder();
        builder.name("ValidName");
        HSClassFile hsClassFile = builder.build();
        assertEquals("ValidName", hsClassFile.name());
    }

    @Test
    public void testMajorVersion() {
        HSClassFile hsClassFile = new HSClassFile.Builder().majorVersion(1).build();
        assertEquals(1, hsClassFile.majorVersion());
    }

    @Test
    public void testMajorVersionEdgeCase() {
        HSClassFile hsClassFile = new HSClassFile.Builder().majorVersion(Integer.MAX_VALUE).build();
        assertEquals(Integer.MAX_VALUE, hsClassFile.majorVersion());
    }

    @Test
    public void testToString() {
        HSClassFile hsClassFile = new HSClassFile.Builder().name("TestName").build();
        String toString = hsClassFile.toString();
        assertTrue(toString.contains("TestName"));
    }

    @Test
    public void testEquals() {
        HSClassFile hsClassFile1 = new HSClassFile.Builder().name("TestName").build();
        HSClassFile hsClassFile2 = new HSClassFile.Builder().name("TestName").build();
        assertTrue(hsClassFile1.equals(hsClassFile2));
    }

    @Test
    public void testNotEquals() {
        HSClassFile hsClassFile1 = new HSClassFile.Builder().name("TestName1").build();
        HSClassFile hsClassFile2 = new HSClassFile.Builder().name("TestName2").build();
        assertFalse(hsClassFile1.equals(hsClassFile2));
    }

    @Test
    public void testHashCode() {
        HSClassFile hsClassFile = new HSClassFile.Builder().name("TestName").build();
        int hashCode = hsClassFile.hashCode();
        assertNotNull(hashCode);
    }
}
