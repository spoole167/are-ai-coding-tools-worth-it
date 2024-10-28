import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClaudAITests {

    private HSClassFile classFile;
    private HSClassFile.Builder builder;
    private HSClassFile.ConstantPool constantPool;

    @BeforeEach
    void setUp() {
        builder = HSClassFile.builder();
        constantPool = new HSClassFile.ConstantPool(100);
        builder.constantPool(constantPool);
    }

    @Test
    void testBasicClassFileCreation() {
        builder.accessFlags(0x0001); // public
        builder.thisClass(1);
        builder.superClass(2);
        classFile = builder.build();

        assertNotNull(classFile);
        assertTrue(classFile.isPublic());
        assertFalse(classFile.isInterface());
        assertFalse(classFile.isEnum());
    }

    @Test
    void testClassTypes() {
        builder.accessFlags(0x0200); // interface
        classFile = builder.build();
        assertTrue(classFile.isInterface());
        assertEquals(HSClassFile.Type.INTERFACE, classFile.type());

        builder = HSClassFile.builder();
        builder.constantPool(constantPool);
        builder.accessFlags(0x4000); // enum
        classFile = builder.build();
        assertTrue(classFile.isEnum());
        assertEquals(HSClassFile.Type.ENUM, classFile.type());
    }

    @Test
    void testJavaVersion() {
        builder.major(52);
        classFile = builder.build();
        assertEquals("Java 8", classFile.javaVersion());
    }

    @Test
    void testMethodsAndFields() {
        HSClassFile.Builder.MethodBuilder methodBuilder = builder.newMethod();
        methodBuilder.accessFlags(0x0001); // public
        methodBuilder.nameIndex(1);
        methodBuilder.descriptorIndex(2);
        HSClassFile.Method method = methodBuilder.build();

        HSClassFile.Builder.FieldBuilder fieldBuilder = builder.newField();
        fieldBuilder.accessFlags(0x0001); // public
        fieldBuilder.nameIndex(3);
        fieldBuilder.descriptorIndex(4);
        HSClassFile.Field field = fieldBuilder.build();

        builder.methods(1, new HSClassFile.Method[]{method});
        builder.fields(1, new HSClassFile.Field[]{field});

        classFile = builder.build();

        assertEquals(1, classFile.methods().size());
        assertEquals(1, classFile.fields().size());
        assertTrue(classFile.methods().bySignature("test()V").isPublic());
        assertTrue(classFile.fields().byName("testField").isPublic());
    }

    @Test
    void testConstantPool() {
        constantPool.addUtf8("test".getBytes());
        constantPool.addClass(1);
        constantPool.addMethodref(1, 2);

        assertEquals(HSClassFile.ConstantPoolEntry.CONSTANT_Utf8, constantPool.getEntryType(1));
        assertEquals(HSClassFile.ConstantPoolEntry.CONSTANT_Class, constantPool.getEntryType(2));
        assertEquals(HSClassFile.ConstantPoolEntry.CONSTANT_Methodref, constantPool.getEntryType(3));

        assertEquals("test", constantPool.getUTF8AsString(1));
        assertEquals(1, constantPool.getClassReference(2));
        assertEquals(1, constantPool.getMethodRefClass(3));
        assertEquals(2, constantPool.getMethodRefType(3));
    }

    @Test
    void testAttributes() {
        HSClassFile.Builder.AttributeBuilder attrBuilder = builder.newAttribute();
        attrBuilder.attributeNameindex(1);
        attrBuilder.attributeLength(10);
        attrBuilder.data(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        HSClassFile.Attribute attr = attrBuilder.build();

        builder.attributes(new HSClassFile.Attribute[]{attr});
        classFile = builder.build();

        assertTrue(classFile.attributes().hasAttribute("test"));
    }

    @Test
    void testNullPointerExceptions() {
        assertThrows(NullPointerException.class, () -> {
            builder.constantPool(null);
            classFile = builder.build();
            classFile.name();
        });

        assertThrows(NullPointerException.class, () -> {
            HSClassFile.ConstantPool nullPool = new HSClassFile.ConstantPool(1);
            nullPool.getUTF8AsString(1);
        });
    }

    @Test
    void testIllegalArgumentExceptions() {
        assertThrows(IllegalArgumentException.class, () -> {
            HSClassFile.ConstantPool smallPool = new HSClassFile.ConstantPool(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            constantPool.getClassReference(0);
        });
    }

    @Test
    void testEdgeCases() {
        // Test with maximum possible access flags
        builder.accessFlags(0xFFFF);
        classFile = builder.build();
        assertTrue(classFile.isPublic());
        assertTrue(classFile.isInterface());
        assertTrue(classFile.isEnum());

        // Test with empty constant pool
        HSClassFile.ConstantPool emptyPool = new HSClassFile.ConstantPool(1);
        builder.constantPool(emptyPool);
        classFile = builder.build();
        assertNull(classFile.name());

        // Test with maximum possible major version
        builder.major(Integer.MAX_VALUE);
        classFile = builder.build();
        assertNotNull(classFile.javaVersion());
    }

   // @Test
   // void testParseExceptions() {
   //     constantPool.addString(1);
    //    assertThrows(HSClassFile.ParseException.class, () -> {
     //       constantPool.getStringReference(1);
     //   });
    //}
//}

}
