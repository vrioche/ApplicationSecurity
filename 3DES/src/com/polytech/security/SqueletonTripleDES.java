package com.polytech.security;



import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.util.*;

public class SqueletonTripleDES{


	static public void main(String[] argv){
		
		Provider prov = new org.bouncycastle.jce.provider.BouncyCastleProvider();
		Security.addProvider(prov);
		
		try{
	
			if(argv.length>0){
			
				// Create a TripleDES object 
				SqueletonTripleDES the3DES = new SqueletonTripleDES();
			
				if(argv[0].compareTo("-ECB")==0){
					// EBC mode
				  	// encrypt EBC mode
				  	Vector Parameters= 
					  	the3DES.encryptECB(
					  			new FileInputStream(new File(argv[1])),  	// clear text file 
				   	  			new FileOutputStream(new File(argv[2])), 	// file encrypted
				   	  			"DES", 										// KeyGeneratorName
				   	  			"DES/ECB/NoPadding"); 						// CipherName 
				  	// decrypt EBC mode
				  	the3DES.decryptECB(Parameters,				 			// the 3 DES keys
				  				new FileInputStream(new File(argv[2])),  	// the encrypted file 
				   	  			new FileOutputStream(new File(argv[3])),	// the decrypted file
				   	  			"DES/ECB/NoPadding"); 		  				// CipherName
				}	
				else if(argv[0].compareTo("-CBC")==0){
					// decryption
				  	// encrypt CBC mode
				  	Vector Parameters = 
					  	the3DES.encryptCBC(
					  			new FileInputStream(new File(argv[1])),  	// clear text file 
				   	  			new FileOutputStream(new File(argv[2])), 	// file encrypted
				   	  			"DES", 										// KeyGeneratorName
					  			"DES/CBC/NoPadding"); 						// CipherName
				   	  			//"DES/CBC/PKCS5Padding"); 					// CipherName 
				  	// decrypt CBC mode	
				  	the3DES.decryptCBC(
				  				Parameters,				 					// the 3 DES keys
			  					new FileInputStream(new File(argv[2])),  	// the encrypted file 
			  					new FileOutputStream(new File(argv[3])),	// the decrypted file
				  				"DES/CBC/NoPadding"); 						// CipherName			
				  				//"DES/CBC/PKCS5Padding"); 		  			// CipherName	  
				}
			
			}
			
			else{
				System.out.println("java TripleDES -EBC clearTextFile EncryptedFile DecryptedFile");
				System.out.println("java TripleDES -CBC clearTextFile EncryptedFile DecryptedFile");
			} 
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("java TripleDES -EBC clearTextFile EncryptedFile DecryptedFile");
			System.out.println("java TripleDES -CBC clearTextFile EncryptedFile DecryptedFile");
		}
	}

	
	/**
	 * 3DES ECB Encryption
	 */
	private Vector encryptECB(FileInputStream in, 
							FileOutputStream out, 
							String KeyGeneratorInstanceName, 
							String CipherInstanceName){
		try{
		
			// GENERATE 3 DES KEYS
			//Get a key Generator for DES Algorithm
			KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyGeneratorInstanceName);
            //K1
            Key K1 = keyGenerator.generateKey();
			//K2
			Key K2 = keyGenerator.generateKey();
			//K3
			Key K3 = keyGenerator.generateKey();

			Vector<Key> vKeys = new Vector<>();
			vKeys.add(K1);
			vKeys.add(K2);
			vKeys.add(K3);


			// CREATE A DES CIPHER OBJECT 
				// WITH CipherInstanceName
				// FOR ENCRYPTION 
				// WITH THE FIRST GENERATED DES KEY
            Cipher C1 = Cipher.getInstance(CipherInstanceName);
            C1.init(Cipher.ENCRYPT_MODE, K1);
			
			// CREATE A DES CIPHER OBJECT 
				// WITH CipherInstanceName
				// FOR DECRYPTION
				// WITH THE SECOND GENERATED DES KEY
            Cipher C2 = Cipher.getInstance(CipherInstanceName);
            C2.init(Cipher.DECRYPT_MODE, K2);
				
			// CREATE A DES CIPHER OBJECT 
				// WITH CipherInstanceName 
				// FOR ENCRYPTION
				// WITH THE THIRD GENERATED DES KEY
			Cipher C3 = Cipher.getInstance(CipherInstanceName);
			C3.init(Cipher.ENCRYPT_MODE, K3);

			// GET THE MESSAGE TO BE ENCRYPTED FROM IN
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            int content;
            while ((content = in.read()) != -1) {
                dataStream.write(content);
            }
            byte[] message = dataStream.toByteArray();
            dataStream.close();

			// CIPHERING     
				// CIPHER WITH THE FIRST KEY
				// DECIPHER WITH THE SECOND KEY
				// CIPHER WITH THE THIRD KEY
				// write encrypted file
            byte[] encryptedMessage = C3.doFinal(C2.doFinal(C1.doFinal(message)));

			// WRITE THE ENCRYPTED DATA IN OUT
            BufferedOutputStream buff = new BufferedOutputStream(out);
            buff.write(encryptedMessage);
            buff.close();

			// return the DES keys list generated		
			return vKeys;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 3DES ECB Decryption 
	 */
	private void decryptECB(Vector Parameters, 
						FileInputStream in, 
						FileOutputStream out, 
						String CipherInstanceName){
		try{

			// CREATE A DES CIPHER OBJECT 
				// WITH CipherInstanceName
				// FOR DECRYPTION 
				// WITH THE THIRD GENERATED DES KEY
			Cipher C1 = Cipher.getInstance(CipherInstanceName);
			C1.init(Cipher.DECRYPT_MODE, (Key) Parameters.get(2));

			// CREATE A DES CIPHER OBJECT 
				// WITH CipherInstanceName
				// FOR ENCRYPTION
				// WITH THE SECOND GENERATED DES KEY
            Cipher C2 = Cipher.getInstance(CipherInstanceName);
            C2.init(Cipher.ENCRYPT_MODE, (Key) Parameters.get(1));
				
			// CREATE A DES CIPHER OBJECT WITH DES/EBC/PKCS5PADDING FOR ENCRYPTION
				// WITH CipherInstanceName
				// FOR DECRYPTION
				// WITH THE FIRST GENERATED DES KEY
            Cipher C3 = Cipher.getInstance(CipherInstanceName);
            C3.init(Cipher.DECRYPT_MODE, (Key) Parameters.get(0));
			
			// GET THE ENCRYPTED DATA FROM IN
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            int content;
            while ((content = in.read()) != -1) {
                dataStream.write(content);
            }
            byte[] encryptedMessage = dataStream.toByteArray();
            dataStream.close();

			// DECIPHERING     
				// DECIPHER WITH THE THIRD KEY
				// 	CIPHER WITH THE SECOND KEY
				// 	DECIPHER WITH THE FIRST KEY
            byte[] message = C3.doFinal(C2.doFinal(C1.doFinal(encryptedMessage)));

			// WRITE THE DECRYPTED DATA IN OUT
            BufferedOutputStream buff = new BufferedOutputStream(out);
            buff.write(message);
            buff.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	  
	/**
	 * 3DES ECB Encryption
	 */
	private Vector encryptCBC(FileInputStream in, 
							FileOutputStream out, 
							String KeyGeneratorInstanceName, 
							String CipherInstanceName){
		try{

		    Vector vectorParams = new Vector();

			// GENERATE 3 DES KEYS
			KeyGenerator kg = KeyGenerator.getInstance(KeyGeneratorInstanceName);
			SecretKey secretKey1 = kg.generateKey();
			SecretKey secretKey2 = kg.generateKey();
			SecretKey secretKey3 = kg.generateKey();

			vectorParams.add(secretKey1);
			vectorParams.add(secretKey2);
			vectorParams.add(secretKey3);
		
			// CREATE A DES CIPHER OBJECT 
				// WITH CipherInstanceName
				// FOR ENCRYPTION 
				// WITH THE FIRST GENERATED DES KEY
            Cipher cipher1 = Cipher.getInstance(CipherInstanceName);

                // GENERATE THE IV (based on the length of cipher1)
            SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
            byte[] iv1 = new byte[cipher1.getBlockSize()];
            randomSecureRandom.nextBytes(iv1); //permet d'avoir un nombre random de taille iv bits
            IvParameterSpec ivParams1 = new IvParameterSpec(iv1);
            vectorParams.add(ivParams1);


            cipher1.init(Cipher.ENCRYPT_MODE, secretKey1, ivParams1);

			// CREATE A DES CIPHER OBJECT 
				// WITH CipherInstanceName
				// FOR DECRYPTION
				// WITH THE SECOND GENERATED DES KEY
            Cipher cipher2 = Cipher.getInstance(CipherInstanceName);

            byte[] iv2 = new byte[cipher2.getBlockSize()];
            randomSecureRandom.nextBytes(iv2);
            IvParameterSpec ivParams2 = new IvParameterSpec(iv2);
            vectorParams.add(ivParams2);

            cipher2.init(Cipher.DECRYPT_MODE, secretKey2, ivParams2);

			// CREATE A DES CIPHER OBJECT 
				// WITH CipherInstanceName 
				// FOR ENCRYPTION
				// WITH THE THIRD GENERATED DES KEY
            Cipher cipher3 = Cipher.getInstance(CipherInstanceName);

            byte[] iv3 = new byte[cipher3.getBlockSize()];
            randomSecureRandom.nextBytes(iv3);
            IvParameterSpec ivParams3 = new IvParameterSpec(iv3);
            vectorParams.add(ivParams3);

            cipher3.init(Cipher.ENCRYPT_MODE, secretKey3, ivParams3);

			// GET THE DATA TO BE ENCRYPTED FROM IN 
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            int content;
            while ((content = in.read()) != -1) {
                dataStream.write(content);
            }
            byte[] plainText = dataStream.toByteArray();

			// CIPHERING     
				// CIPHER WITH THE FIRST KEY
				// DECIPHER WITH THE SECOND KEY
				// CIPHER WITH THE THIRD KEY
            byte[] cipherText = cipher3.doFinal(cipher2.doFinal(cipher1.doFinal(plainText)));

			// WRITE THE ENCRYPTED DATA IN OUT
            BufferedOutputStream buff = new BufferedOutputStream(out);
            buff.write(cipherText);
            buff.close();

            // return the DES keys list generated
			return vectorParams;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 3DES ECB Decryption 
	 */
	private void decryptCBC(Vector Parameters, 
						FileInputStream in, 
						FileOutputStream out, 
						String CipherInstanceName){
		try{

		    SecretKey secretKey1 = (SecretKey) Parameters.get(0);
		    SecretKey secretKey2 = (SecretKey) Parameters.get(1);
		    SecretKey secretKey3 = (SecretKey) Parameters.get(2);

		    IvParameterSpec ivParams1 = (IvParameterSpec) Parameters.get(3);
            IvParameterSpec ivParams2 = (IvParameterSpec) Parameters.get(4);
            IvParameterSpec ivParams3 = (IvParameterSpec) Parameters.get(5);

            // CREATE A DES CIPHER OBJECT
				// WITH CipherInstanceName
				// FOR DECRYPTION 
				// WITH THE THIRD GENERATED DES KEY
            Cipher cipher1 = Cipher.getInstance(CipherInstanceName);
            cipher1.init(Cipher.DECRYPT_MODE, secretKey3, ivParams3);

			// CREATE A DES CIPHER OBJECT 
				// WITH CipherInstanceName
				// FOR DECRYPTION
				// WITH THE SECOND GENERATED DES KEY
            Cipher cipher2 = Cipher.getInstance(CipherInstanceName);
            cipher2.init(Cipher.ENCRYPT_MODE, secretKey2, ivParams2);

			// CREATE A DES CIPHER OBJECT WITH DES/EBC/PKCS5PADDING FOR ENCRYPTION
				// WITH CipherInstanceName
				// FOR ENCRYPTION
				// WITH THE FIRST GENERATED DES KEY
            Cipher cipher3 = Cipher.getInstance(CipherInstanceName);
            cipher3.init(Cipher.DECRYPT_MODE, secretKey1, ivParams1);

			// GET ENCRYPTED DATA FROM IN
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            int content;
            while ((content = in.read()) != -1) {
                dataStream.write(content);
            }
            byte[] cipherText = dataStream.toByteArray();

			// DECIPHERING     
				// DECIPHER WITH THE THIRD KEY
				// 	CIPHER WITH THE SECOND KEY
				// 	DECIPHER WITH THE FIRST KEY
            byte[] plainText = cipher3.doFinal(cipher2.doFinal(cipher1.doFinal(cipherText)));

			// WRITE THE DECRYPTED DATA IN OUT
            BufferedOutputStream buff = new BufferedOutputStream(out);
            buff.write(plainText);
            buff.close();

        }catch(Exception e){
			e.printStackTrace();
		}

	}
	  

}