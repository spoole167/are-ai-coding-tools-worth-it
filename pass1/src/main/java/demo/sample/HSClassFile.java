package demo.sample;

import java.util.*;
import java.util.stream.Stream;

public class HSClassFile {



    public enum Type { CLASS,INTERFACE,ENUM , UNKOWN}
    public String name() { return constantPool.getResolvedClassName(this_class); }

public String toString() { return name(); };
    public boolean isClass() {
        return !isEnum() && !isInterface();
    }

    public boolean isPublic() {
        return (access_flags & 0x0001 ) == 0x0001;
    }

    public AttributeSet attributes() {
        return attributes;
    }
    public Type type() {
        if(isClass()) return Type.CLASS;
        if(isEnum()) return Type.ENUM;
        if(isInterface()) return Type.INTERFACE;
        return Type.UNKOWN;
    }
    public boolean isFinal() {
        return (access_flags & 0x0010 ) == 0x0010;
    }


    public String javaVersion () {
        return JavaVersions.toVersion(major);
    }
    public int majorVersion() {
        return major;
    }

    public MethodSet methods() {
        return methods;
    }

    public int accessFlags() {
        return access_flags;
    }

    public Method methodBySignature(String s) {
        return methods.bySignature(s);
    }

    public FieldSet fields() {
        return fields;
    }

    public String packageName() { String name=name();
        if(name==null) return null;
        int ls=name.lastIndexOf("/");
        if(ls<0) return "";
        return name.substring(0,ls).replace("/",".");

    }
    public String shortName() {
        String name = name();
        if (name == null) return null;
        int ls = name.lastIndexOf("/");
        if (ls >= 0) {
            name = name.substring(ls + 1);
        }
        return name.replace("$",".");


    }


    public static final class MethodSet {

        private Map<String,Method> bySignature =new TreeMap<>();
        private Method[] methods;

        private MethodSet(Method[] methods) {
            this.methods=methods;
            for(Method m:methods) {
                bySignature.put(m.signature(),m);
            }
        }

        public Stream<Method> stream() {
            return Arrays.stream(methods);
        }
        public Method bySignature(String d ){
            return bySignature.get(d);
        }
        public int size() {
            return methods.length;
        }
    }


    public static final class FieldSet {

        private Map<String,Field> byName =new TreeMap<>();
        private Field[] fields;

        private FieldSet(Field[] fields) {
            this.fields=fields;
            for(Field f:fields) {
                byName.put(f.name(),f);
            }
        }

        public Stream<Field> stream() {
            return Arrays.stream(fields);
        }

        public Field byName(String d ){
            return byName.get(d);
        }
        public int size() {
            return fields.length;
        }
    }


    public final class Attribute {
        private int attribute_name_index;
        private long attribute_length;
        private byte[] data;

        private Attribute() {

        }

        public String name() { return constantPool.getUTF8AsString(attribute_name_index); }

    }
    public final class Field {
        private  int access_flags;
        private  int name_index;
        private  int descriptor_index;
        private  int attributes_count;
        private Attribute[] attributes;

    private Field() {

    }
        public boolean isPublic() {
            return (access_flags & 0x0001 ) == 0x0001;
        }


        public int accessFlags() {
            return access_flags;
        }
        public boolean isProtected() {
            return (access_flags & 0x0004 ) == 0x0004;
        }
        public String name() { return constantPool.getUTF8AsString(name_index); }
    }

    public final class Method {


        int access_flags;
        int name_index;
        int descriptor_index;
        AttributeSetImpl attributes;

    public AttributeSet attributes() {
        return attributes;
    }
        public int accessFlags() {
            return access_flags;
        }


        public boolean isPublic() {
            return (access_flags & 0x001) == 0x0001;

        }


        public boolean isProtected() {
            return (access_flags & 0x004) == 0x0004;
        }


        private Method() {

        }

        public String name() {
            return constantPool.getUTF8AsString(name_index);

        }
        public String descriptor() {
            return constantPool.getUTF8AsString(descriptor_index);
        }

        public String signature() {
            return name()+descriptor();
        }
    }

   private int minor;
    private int major;
    private ConstantPool constantPool;
    private int access_flags;
    private int this_class;
    private int super_class;

    private int interface_count;
    private int[] interfaces;

    private int fields_count;
    private FieldSet fields;

    private int methods_count;
    private  MethodSet methods;
    private AttributeSetImpl attributes;
    private boolean extraneousData;
   private long header;
    private boolean valid;
    private byte[] digest;

    public boolean isInterface() { return (access_flags & 0x0200 ) == 0x0200; }

    public boolean isEnum() {   return (access_flags & 0x4000 ) == 0x04000; }

    public static class Builder {

        private HSClassFile cf=new HSClassFile();


        public Builder.FieldBuilder newField() {
            return new FieldBuilder();
        }

        public Builder.AttributeBuilder newAttribute() {
            return new AttributeBuilder();
        }

        public class AttributeBuilder {

            private Attribute a=cf.new Attribute();

            private AttributeBuilder() {

            }

            public void attributeNameindex(int i) {
                a.attribute_name_index=i;
            }

            public void attributeLength(long l) {
                a.attribute_length=l;
            }

            public void data(byte[] bytes) {
                a.data=bytes;
            }

            public Attribute build() {
                Attribute x=a;
                a=null;
                return x;
            }
        }

        public class FieldBuilder {
            private Field f;
            private FieldBuilder() {
                f=cf.new Field();
            }

            public void accessFlags(int i) {
                f.access_flags=i;
            }

            public void nameIndex(int i) {
                f.name_index=i;
            }

            public void descriptorIndex(int i) {
                f.descriptor_index=i;
            }

            public void attributes(int attributes_count, Attribute[] attrs) {
                f.attributes_count=attributes_count;
                f.attributes=attrs;
            }

            public Field build() {
                Field x=f;
                f=null;
                return x;
            }
        }
        public class MethodBuilder {

            private Method m;

            private MethodBuilder() {
                    m=cf.new Method();
            }

            public Method build() {
                Method x=m;
                m=null;
                return x;
            }

            public void accessFlags(int i) {
                m.access_flags=i;
            }

            public void nameIndex(int i) {
                m.name_index=i;
            }

            public void descriptorIndex(int i) {
                m.descriptor_index=i;
            }

            public void attributes(Attribute[] attrs) {
                m.attributes= new AttributeSetImpl(attrs);
            }
        }


        public void header(long header) {
            cf.header=header;
        }

        public Builder constantPool(ConstantPool cp) {
            cf.constantPool=cp;
            return this;
        }

        public Builder accessFlags(int accessFlags) {
            cf.access_flags =accessFlags;
            return this;
        }

        public Builder thisClass(int thisClass) {
            cf.this_class=thisClass;
            return this;
        }

        public Builder superClass(int superClass) {
            cf.super_class=superClass;
            return this;
        }



        public void interfaces(int ic,int[] interfaces) {
            cf.interface_count=ic;
            cf.interfaces=interfaces;
        }

        public void fields(int fc,Field[] fields) {
            cf.fields=new FieldSet(fields);
            cf.fields_count=fc;
        }

        public void attributes(Attribute[] attrs) {
            cf.attributes=new AttributeSetImpl(attrs);
        }

        public void valid(boolean b) {
            cf.valid=b;
        }

        public void digest(byte[] digest) {
            cf.digest=digest;
        }

        public HSClassFile build() {
            HSClassFile x=cf;
            cf=null;
            return x;
        }

        public Builder minor(int minor) {
            cf.minor=minor;
            return this;
        }

        public Builder major(int major) {
            cf.major=major;
            return this;
        }

        public void methods(int methods_count, Method[] methods) {
            cf.methods_count=methods_count;
            cf.methods=new MethodSet(methods);
        }

        public MethodBuilder newMethod() {
            return new MethodBuilder();
        }
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class ConstantPool {



        private Object[][] table;
        private int tIndex;

        private boolean verbose=false;
        public ConstantPool(int cpc) {
            table=new Object[cpc][];
        }
        public void addClass(int index) {

            insert(new Object[]{ConstantPoolEntry.CONSTANT_Class,index});

            if(verbose) System.out.println(tIndex+" Constant: Class "+index);
        }

        private void insert(Object[] entry) {
            if(entry==null) throw new RuntimeException("entry is null");
            if(entry.length<1) throw new RuntimeException("entry is short "+entry.length);
            if(entry[0]==null) throw new RuntimeException("entry is missing type");
            tIndex++;
            table[tIndex]=entry;
            if(verbose) System.out.println("added "+tIndex+" : "+entry[0]);
        }

        public void addFieldref(int class_index, int name_and_type_index) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_Fieldref,class_index,name_and_type_index});
        }
        public void addMethodref(int class_index, int type_index) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_Methodref,class_index,type_index});
            if(verbose) System.out.println(tIndex+" Constant: Method Ref "+class_index+"/"+type_index);
        }
        public void addInterfaceMethodref(int class_index, int name_and_type_index) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_InterfaceMethodref,class_index,name_and_type_index});
        }
        public void addString(int string_index) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_String,string_index});
            if(verbose) System.out.println(tIndex+" Constant: String  "+string_index);

        }
        public void addInteger(long ivalue) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_Integer,ivalue});
            if(verbose) System.out.println(tIndex+" Constant: Integer  "+ivalue);


        }
        public void addFloat(long f) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_Float,f});
            if(verbose) System.out.println(tIndex+" Constant: Float");

        }
        public void addLong	(byte[] l) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_Long,l});
            if(verbose) System.out.println(tIndex+" Constant: Long");
            insert(new Object[]{ConstantPoolEntry.CONSTANT_EMPTY});

        }
        public void addDouble(byte[] d) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_Double,d});
            if(verbose) System.out.println(tIndex+" Constant: Double");
            insert(new Object[]{ConstantPoolEntry.CONSTANT_EMPTY});
        }
        public void addNameAndType	(int name_index, int descriptor) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_NameAndType,name_index,descriptor});
            if(verbose) System.out.println(tIndex+" Constant: Name and Type "+name_index+"/"+descriptor);
        }
        public void addUtf8(byte[] data) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_Utf8,data});
            if(verbose) System.out.println(tIndex+" Constant: UTF8 String "+data.length+" long ("+new String(data));
        }
        public void addMethodHandle	(int reference_kind, int reference_index) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_MethodHandle,reference_kind,reference_index});
        }
        public void addMethodType(int di) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_MethodType,di});
        }
        public void addInvokeDynamic(int bootstrap_method_attr_index, int name_and_type_index) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_InvokeDynamic,bootstrap_method_attr_index,name_and_type_index});
        }

        public void addModule(int ni) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_Module,ni});
        }


        public void addPackage(int ni) {
            insert(new Object[]{ConstantPoolEntry.CONSTANT_Package,ni});
        }
        public int getClassReference(int idx) {
            Object[] data=table[idx];
            return (int) table[idx][1];
        }

        public int getStringReference(int idx) throws ParseException {
            Object[] data=table[idx];
            checkType(idx,data, ConstantPoolEntry.CONSTANT_String);
            return (int) table[idx][1];
        }

        private void checkType(int idx,Object[] data, ConstantPoolEntry expected) throws ParseException {
            ConstantPoolEntry type= (ConstantPoolEntry) data[0];
            if(type!=expected) throw new ParseException("constant pool entry "+idx+" expected "+expected+" found "+type);
        }

        public ConstantPoolEntry getEntryType(int idx) {
            Object[] data=table[idx];
            return (ConstantPoolEntry) table[idx][0];
        }

        public int getMethodRefClass(int idx) {
            Object[] data=table[idx];
            return (int) table[idx][1];
        }

        public int getMethodRefType(int idx) {
            Object[] data=table[idx];
            return (int) table[idx][2];
        }

        public int getNameTypeReference(int idx) {
            Object[] data=table[idx];
            return (int) table[idx][2];
        }

        public byte[] getUTF8(int idx) {
             Object[] data=table[idx];
             if(data==null) throw new RuntimeException("empty at "+idx);
                if(data[0]!= ConstantPoolEntry.CONSTANT_Utf8) throw new RuntimeException(" got "+data[0]);
            return (byte[]) table[idx][1];
        }

        public String getUTF8AsString(int idx) {
           return new String(getUTF8(idx));
        }

        public int getString(int idx) {
            Object[] data=table[idx];
            return (int) table[idx][1];
        }

        public String getResolvedClassName(int idx) {

            int classRef= getClassReference(idx);
            return getUTF8AsString(classRef);

        }

        public String getResolvedMethodName(int idx) {

            int class_index=getMethodRefClass(idx);
            int type_index=getMethodRefType(idx);
            int classRef=getClassReference(class_index);
            int methodRef=getNameTypeReference(type_index);
            String className=getUTF8AsString(classRef);
            String methodName=getUTF8AsString(methodRef);

            return className+"@"+methodName;

        }

        public String getResolvedStringRef(int idx) throws ParseException {
             int string_index=getStringReference(idx);
             return getUTF8AsString(string_index);

        }

    }

    public enum ConstantPoolEntry {
        CONSTANT_Class(7),
        CONSTANT_Fieldref ( 9),
        CONSTANT_Methodref ( 10),
        CONSTANT_InterfaceMethodref ( 11),
        CONSTANT_String ( 8),
        CONSTANT_Integer ( 3),
        CONSTANT_Float ( 4),
        CONSTANT_Long ( 5),
         CONSTANT_Double ( 6),
         CONSTANT_NameAndType ( 12),
         CONSTANT_Utf8 ( 1),
        CONSTANT_MethodHandle ( 15),
         CONSTANT_MethodType ( 16),
         CONSTANT_InvokeDynamic ( 18),

        CONSTANT_Unknown(0),
        CONSTANT_Module(19),
        CONSTANT_Package(20),
        CONSTANT_EMPTY(999);

        private int tagID;

        private static final Map<Integer, ConstantPoolEntry> tagMap = new HashMap<>();

        static {
            for (ConstantPoolEntry e: values()) {
                tagMap.put(e.tagID, e);
            }
        }
        private ConstantPoolEntry(int tagID ) {
            this.tagID=tagID;
        }


        public static ConstantPoolEntry entry(int tag) {
                ConstantPoolEntry e=tagMap.get(tag);
                if(e==null) e= ConstantPoolEntry.CONSTANT_Unknown;
                return e;
        }
    }

    public final static class AttributeSetImpl implements AttributeSet {
        private final Map<String, List<Attribute>> attributeByName = new TreeMap<>();
        private final Attribute[] attributes;


        private AttributeSetImpl(Attribute[] attrs) {
            this.attributes = attrs;
            for (Attribute a : attrs) {
                add(a);
            }
        }


        private void add(Attribute attribute) {
            String name = attribute.name();
            List<Attribute> list = attributeByName.get(name);
            if (list == null) {
                list = new LinkedList<>();
                attributeByName.put(name, list);
            }
            list.add(attribute);
        }

        public boolean hasAttribute(String name) {
            return attributeByName.containsKey(name);
        }

        @Override
        public boolean hasAttribute(HSAttributeName name) {
            return hasAttribute(name.name());
        }
    }

    public static interface AttributeSet {

        public boolean hasAttribute(String name);
        public boolean hasAttribute(HSAttributeName name);
    }

    public static class ParseException extends Exception {
        public ParseException(String s) {
            super(s);
        }
    }

    public static class JavaVersions {


        public static String toVersion(int majorVersion) {


            if(majorVersion<45) {
                // not java
                return "unknown";
            }

            if(majorVersion<=46) {
                return "1.2";
            }
            if(majorVersion<=47) {
                return "1.3";
            }
            if(majorVersion<=48) {
                return "1.4";
            }
            if(majorVersion<=49) {
                return "5.0";
            }
            int diff=majorVersion-50;
            return ""+(6+diff);

        }


    }


    public static enum HSAttributeName {


        // java 7

        ConstantValue, Code, StackMapTable,
        Exceptions,
        InnerClasses,
        EnclosingMethod,
        Synthetic,
        Signature,
        SourceFile,
        SourceDebugExtension,
        LineNumberTable,
        LocalVariableTable,
        LocalVariableTypeTable,
        Deprecated,
        RuntimeVisibleAnnotations,
        RuntimeInvisibleAnnotations,
        RuntimeVisibleParameterAnnotations,
        RuntimeInvisibleParameterAnnotations,
        AnnotationDefault,
        BootstrapMethods,

        /// 9

        MethodParameters,
        Module,
        ModulePackages,
        ModuleMainClass,

        // 11
        NestHost,
        NestMembers,
        // 16
        Record,
        // 17
        PermittedSubclasses



    }

}
