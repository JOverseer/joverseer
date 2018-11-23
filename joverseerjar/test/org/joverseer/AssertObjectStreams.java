package org.joverseer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

class AssertObjectStreams {
        InputStream expectedStream ;
        InputStream actualStream ;
        AssertObjectStreams(InputStream expectedStream, InputStream actualStream ) {
        	this.expectedStream = expectedStream;
        	this.actualStream = actualStream;
        }
        public static short readShort(InputStream is) throws IOException {
        	short value = (short)is.read();
        	value = (short)((value << 8)  + (short)is.read());
        	return value;
        }
        public static int readInt(InputStream is) throws IOException {
        	int value = is.read();
        	value = (value << 8)  + is.read();
        	value = (value << 8)  + is.read();
        	value = (value << 8)  + is.read();
        	return value;
        	
        }
        public static long readLong(InputStream is) throws IOException {
        	long value = is.read();
        	value = (value << 8)  + is.read();
        	value = (value << 8)  + is.read();
        	value = (value << 8)  + is.read();
        	value = (value << 8)  + is.read();
        	value = (value << 8)  + is.read();
        	value = (value << 8)  + is.read();
        	value = (value << 8)  + is.read();
        	return value;
        	
        }
        protected int assertRecordType(int record) throws IOException {
        	int expectedType = this.expectedStream.read();
        	int actualType = this.actualStream.read();
        	assertEquals("Record type",record,actualType);
        	assertEquals("Record type ",expectedType, actualType);
        	return actualType;
        }
        
        protected byte assertByte() throws IOException {
        	int actual = this.actualStream.read(); 
        	assertEquals("Record type",this.expectedStream.read(),actual);
        	return (byte) actual;
        }
        protected short assertShort() throws IOException {
        	short expected = readShort(this.expectedStream);
        	short actual = readShort(this.actualStream);
        	assertEquals("Record type",expected,actual);
        	return actual;
        }
        protected int assertInt() throws IOException {
        	int expected = readInt(this.expectedStream);
        	int actual = readInt(this.actualStream);
        	assertEquals("Record type",expected,actual);
        	return actual;
        }
        protected long assertLong() throws IOException {
        	long expected = readLong(this.expectedStream);
        	long actual = readLong(this.actualStream);
        	assertEquals("Record type",expected,actual);
        	return actual;
        }
        protected void assertByteArray(String prefix,int expectedLength,int actualLength) throws IOException {
        	final int length = (expectedLength > actualLength) ? expectedLength : actualLength;
        	byte[] expectedName = new byte[length];
        	byte[] actualName = new byte[length];
        	int expectedRead = this.expectedStream.read(expectedName,0,expectedLength);
        	assertEquals(prefix,expectedLength,expectedRead);
        	expectedRead = this.actualStream.read(actualName,0,actualLength);
        	assertEquals(prefix,expectedLength,expectedRead);
        	assertArrayEquals("class name", expectedName, actualName);
        }
        
        public void assertStream() throws IOException {
        	assertEquals("Magic number",readShort(this.expectedStream),readShort(this.actualStream));
        	assertEquals("Version number",readShort(this.expectedStream),readShort(this.actualStream));
        	assertContents();
        }
        public void assertContents() {
        	assertContent();
        	assertContents();
        }
        public void assertContent() {
        	int expectedType = assertByte();
        	switch(expectedType) {
        	case java.io.ObjectStreamConstants.TC_NULL: break;
        	case java.io.ObjectStreamConstants.TC_REFERENCE: assertPrevObject(true); break;
        	case java.io.ObjectStreamConstants.TC_CLASSDESC: assertNewClassDesc(true); break;
        	case java.io.ObjectStreamConstants.TC_OBJECT: assertNewObject(false); break;
        	case java.io.ObjectStreamConstants.TC_STRING: assertNewString(); break;
        	case java.io.ObjectStreamConstants.TC_CLASS: assertNewClass(true); break;
        	case java.io.ObjectStreamConstants.TC_ARRAY: assertNewArray(); break;
        	case java.io.ObjectStreamConstants.TC_BLOCKDATA: assertBlockData(true); break;
        	case java.io.ObjectStreamConstants.TC_ENDBLOCKDATA:
        	case java.io.ObjectStreamConstants.TC_RESET: break;
        	case java.io.ObjectStreamConstants.TC_BLOCKDATALONG: assertBlockDataLong(true); break;
        	case java.io.ObjectStreamConstants.TC_EXCEPTION:
        	case java.io.ObjectStreamConstants.TC_LONGSTRING:
        	case java.io.ObjectStreamConstants.TC_PROXYCLASSDESC:
        	case java.io.ObjectStreamConstants.TC_ENUM: assertNewEnum(); break;
        	default:
        		assertEquals("unexpected record type",0,actualType);
        		break;
        	}
        	assertObject();
        	assertBlockData();
        }
        
        private void assertObject(int variant) {
        	if (!skipHeader) {
        		
        	}
		}
		public void assertHandle() throws IOException {
        	assertShort();
        }
        public void assertPrevObject(boolean skipHeader) throws IOException {
        	if (!skipHeader) {
        		this.assertRecordType(java.io.ObjectStreamConstants.TC_REFERENCE);
        	}
        	assertHandle();
        }

        public void assertNewClass(boolean skipHeader) throws IOException {
        	if (!skipHeader) {
        		this.assertRecordType(java.io.ObjectStreamConstants.TC_CLASS);
        	}
        	assertClassDesc(false);
        	assertNewHandle();
        }
        public void assertClassDesc(boolean skipHeader) throws IOException {
        	if (!skipHeader) {
        		this.assertRecordType(java.io.ObjectStreamConstants.TC_CLASS);
        	}
        	int expectedType = this.expectedStream.read();
        	int actualType = this.actualStream.read();
        	assertEquals("Record type ",expectedType, actualType);
        	switch (expectedType) {
        	case java.io.ObjectStreamConstants.TC_CLASSDESC: assertNewClassDesc(expectedType);break;
        	case java.io.ObjectStreamConstants.TC_PROXYCLASSDESC: assertNewClassDesc(expectedType);break;
        	case java.io.ObjectStreamConstants.TC_NULL: break;
        	case java.io.ObjectStreamConstants.TC_REFERENCE: break;
        	default:
        		assertEquals("unexpected record type",0,actualType);
        		break;
        	}
        }
        /**
         * newClassDesc:
         *   TC_CLASSDESC className serialVersionUID newHandle classDescInfo
         *   TC_PROXYCLASSDESC newHandle proxyClassDescInfo
         * @param variant
         */
        public void assertNewClassDesc(int variant) {
        	switch (variant) {
        	case java.io.ObjectStreamConstants.TC_CLASSDESC:
        		assertClassName();
        		assertSerialVersionUID();
        		assertNewHandle();
        		assertClassDescInfo();
        		break;
        	case java.io.ObjectStreamConstants.TC_PROXYCLASSDESC: 
        		assertProxyClassDesc(true);
        	break;
        	default:
        		assertEquals("unexpected record type",0,actualType);
        		break;
        	}
        	
        }
        /**
         * classDescInfo:
         *   classDescFlags fields classAnnotation superClassDesc 
         */
        private void assertClassDescInfo() {
        	assertClassDescFlags();
        	assertFields();
        	assertClassAnnotation();
        	assertSuperClassDesc();
		}
        /**
         * fields:
         *  (short)<count>  fieldDesc[count]
         * @throws IOException 
         */
        private void assertFields() throws IOException {
        	int size = assertShort();
			for(int i = 0; i <size; i++) {
				assertFieldDesc();
			}
		}
        /**
         * fieldDesc:
         *   primitiveDesc
         *   objectDesc
		 *
         * primitiveDesc:
         *   prim_typecode fieldName
		 *
         * objectDesc:
         *   obj_typecode fieldName className1
		 *
          prim_typecode:
            `B'	// byte
            `C'	// char
            `D'	// double
            `F'	// float
            `I'	// integer
            `J'	// long
            `S'	// short
            `Z'	// boolean

          obj_typecode:
            `[`	// array
            `L'	// object

         */
		private void assertFieldDesc() {
        	int expectedType = assertByte();
        	switch(expectedType) {
        	case 'B': assertByte(); assertFieldName();break;	// byte
        	case 'C': assertByte(); assertFieldName();break;	// char
        	case 'D': assertLong(); assertFieldName();break;	// double
        	case 'F': assertInt(); assertFieldName();break;	// float
        	case 'I': assertInt(); assertFieldName();break;	// integer
        	case 'J': assertLong(); assertFieldName();;break;	// long
        	case 'S': assertShort(); assertFieldName();break;	// short
        	case 'Z': assertByte(); assertFieldName();break;	// boolean
        	case '[': assertFieldName(); assertClassName1();break;	// array
        	case 'L': assertFieldName(); assertClassName1();break;	// object
        	default:
        		assertEquals("unexpected record type",0,expectedType);
        		break;
        	}
			
		}
		private void assertClassName1() {
			// TODO Auto-generated method stub
			
		}
		// utf
		private void assertFieldName() throws IOException {
        	int expectedLength = assertShort();
        	assertByteArray("field name", expectedLength, expectedLength);
		}
		/**
         * classDescFlags:
         *   (byte)                  // Defined in Terminal Symbols and
         *                             // Constants
		 * @throws IOException 
         */
		private void assertClassDescFlags() throws IOException {
			assertByte();
		}
		private void assertSerialVersionUID() throws IOException {
			assertLong();
		}
		public void assertNewObject() {
        	assertClassDesc();
        	
        }
        public void assertNewHandle() throws IOException {
        	assertShort();
        }
        public void assertClassName() throws IOException {
        	int expectedLength = this.expectedStream.read();
        	int actualLength = this.actualStream.read();
        	assertEquals("class name length",expectedLength, actualLength);
        	assertByteArray("class name", expectedLength, actualLength);
        }
        public void assertBlockData(boolean skipHeader) throws IOException {
        	if (!skipHeader) {
        		this.assertRecordType(java.io.ObjectStreamConstants.TC_BLOCKDATA);
        	}
        	short length = assertShort();
        	byte[] expected = new byte[length];
        	int read = expectedStream.read(expected, 0, length);
        	assertEquals("block data length",length,read);
        	byte[] actual = new byte[length];
        	read = expectedStream.read(actual, 0, length);
        	assertEquals("block data length",length,read);
        	assertArrayEquals("block data", expected, actual);
        }
        public void assertBlockDataLong(boolean skipHeader) throws IOException {
        	if (!skipHeader) {
        		this.assertRecordType(java.io.ObjectStreamConstants.TC_BLOCKDATALONG);
        	}
        	int length = (int)assertLong(); //TODO fix
        	byte[] expected = new byte[length];
        	int read = expectedStream.read(expected, 0, length);
        	assertEquals("block data length",length,read);
        	byte[] actual = new byte[length];
        	read = expectedStream.read(actual, 0, length);
        	assertEquals("block data length",length,read);
        	assertArrayEquals("block data", expected, actual);
        }
/*        object:
            newObject
            newClass
            newArray
            newString
            newEnum
            newClassDesc
            prevObject
            nullReference
            exception
            TC_RESET

          newClass:
            TC_CLASS classDesc newHandle

          classDesc:
            newClassDesc
            nullReference
            (ClassDesc)prevObject      // an object required to be of type
                                       // ClassDesc

          superClassDesc:
            classDesc


          className:
            (utf)

          serialVersionUID:
            (long)

          proxyClassDescInfo:
            (int)<count> proxyInterfaceName[count] classAnnotation
                superClassDesc

          proxyInterfaceName:

            (utf)

          className1:
            (String)object             // String containing the field's type,
                                       // in field descriptor format

          classAnnotation:
            endBlockData
            contents endBlockData      // contents written by annotateClass

          newArray:
            TC_ARRAY classDesc newHandle (int)<size> values[size]

          newObject:
            TC_OBJECT classDesc newHandle classdata[]  // data for each class

          classdata:
            nowrclass                 // SC_SERIALIZABLE & classDescFlag &&
                                      // !(SC_WRITE_METHOD & classDescFlags)
            wrclass objectAnnotation  // SC_SERIALIZABLE & classDescFlag &&
                                      // SC_WRITE_METHOD & classDescFlags
            externalContents          // SC_EXTERNALIZABLE & classDescFlag &&
                                      // !(SC_BLOCKDATA  & classDescFlags
            objectAnnotation          // SC_EXTERNALIZABLE & classDescFlag&& 
                                      // SC_BLOCKDATA & classDescFlags

          nowrclass:
            values                    // fields in order of class descriptor

          wrclass:
            nowrclass

          objectAnnotation:
            endBlockData
            contents endBlockData     // contents written by writeObject
                                      // or writeExternal PROTOCOL_VERSION_2.

          blockdata:
            blockdatashort
            blockdatalong

          blockdatashort:
            TC_BLOCKDATA (unsigned byte)<size> (byte)[size]

          blockdatalong:
            TC_BLOCKDATALONG (int)<size> (byte)[size]

          endBlockData	:
            TC_ENDBLOCKDATA

          externalContent:          // Only parseable by readExternal
            ( bytes)                // primitive data
              object

          externalContents:         // externalContent written by 
            externalContent         // writeExternal in PROTOCOL_VERSION_1.
            externalContents externalContent

          newString:
            TC_STRING newHandle (utf)
            TC_LONGSTRING newHandle (long-utf)

          newEnum:
            TC_ENUM classDesc newHandle enumConstantName

          enumConstantName:
            (String)object

          prevObject
            TC_REFERENCE (int)handle

          nullReference
            TC_NULL

          exception:
            TC_EXCEPTION reset (Throwable)object	 reset 

          magic:
            STREAM_MAGIC

          version
            STREAM_VERSION

          values:          // The size and types are described by the
                           // classDesc for the current object

          newHandle:       // The next number in sequence is assigned
                           // to the object being serialized or deserialized

          reset:           // The set of known objects is discarded
                           // so the objects of the exception do not
                           // overlap with the previously sent objects 
                           // or with objects that may be sent after 
                           // the exception
*/

    	
	}