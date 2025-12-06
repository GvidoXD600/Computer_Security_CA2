import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.InputMismatchException;
import java.util.Scanner;


//Code for the encryption and decryption of file taken from - https://www.baeldung.com/java-aes-encryption-decryption
public class Main
{

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nWelcome to the File Encryptor/Decryptor!");

        boolean exit = false;
        while(!exit)
        {
            String[] menuOptions = {
                    "0. Exit.",
                    "1. Encrypt File.",
                    "2. Decrypt File."
            };

            int menuChoice = -1;
                try
                {
                    MenuUtility.displayMenu(menuOptions, "---MAIN MENU---");

                    menuChoice = MenuUtility.getMenuChoice(menuOptions.length);

                    switch (menuChoice)
                    {
                        case 1:
                            encryptFile(sc);
                            break;
                        case 2:
                            decryptFile(sc);
                            break;
                        case 0:
                            System.out.println("\nThank you for using the application. Goodbye!");
                            exit = true;
                            break;
                        default:
                            System.out.println("❌ Invalid input. Please enter a number from 1 - 3.");
                    }
                }
                catch (InputMismatchException e)
                {
                    System.out.println("❌ Invalid input. Please enter a numerical menu option.");
                    sc.nextLine();
                }
                catch (Exception e)
                {
                    System.out.println("❌ An unexpected error occurred: " + e.getMessage());
                }


        }
        sc.close();
    }

    public static void encryptFile(Scanner sc) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
       try
       {
           System.out.print("\nWhat file would you like to encrypt? (e.g., my_document.txt)\n> ");
           String inputFileName = sc.nextLine();

           File inputFile = new File(inputFileName);

           if (!inputFile.exists() || inputFile.isDirectory()) {
               System.out.println("❌ Error: File not found or is a directory: " + inputFileName);
               return;
           }

           //Read entire file into memory
           byte[] fileBytes = Files.readAllBytes(inputFile.toPath());

           //Generates random AES Key
           KeyGenerator keyGen =  KeyGenerator.getInstance("AES");
           keyGen.init(128);
           SecretKey key = keyGen.generateKey();

           //Creates a random 12-byte IV for encryption
           byte[] ivBytes = new byte[12];
           SecureRandom secureRandom = new SecureRandom();
           secureRandom.nextBytes(ivBytes);
           GCMParameterSpec ivSpec = new GCMParameterSpec(128, ivBytes);

           //Setting up the cipher for encryption
           Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
           cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

           // Encrypt the file
           byte[] encryptedBytes = cipher.doFinal(fileBytes);

           // Combine IV + encrypted data
           ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
           outputStream.write(ivBytes);
           outputStream.write(encryptedBytes);

           // Saves the encrypted data
           Files.write(Paths.get("ciphertext.txt"), outputStream.toByteArray());


           // Shows the AES key for decryption and what the encrypted file is called
           String keyBase64 = Base64.getEncoder().encodeToString(key.getEncoded());
           System.out.println("\nAES Key (save this for decryption): "+ keyBase64);
           System.out.println("Encrypted file saved as: ciphertext.txt");

       }
       catch (Exception e)
       {
           System.out.println("❌ Error during encryption: " + e.getMessage());
       }

    }

    public static void decryptFile(Scanner sc)
    {
        try
        {
            File encryptedFile;
            String encryptedFileName;

            while (true)
            {
                System.out.println("\nEnter the encrypted file name (e.g., ciphertext.txt)\n>");
                encryptedFileName = sc.nextLine();

                encryptedFile = new File(encryptedFileName);
                if (!encryptedFile.exists() || encryptedFile.isDirectory())
                {
                    System.out.println("❌ Error: File not found or is a directory: " + encryptedFileName);
                }
                else
                {
                    break;
                }
            }

            //Asks the user for AES Key
            System.out.println("Enter the AES Key used for encryption");
            String keyBase64 = sc.nextLine().trim();

            // Decode the base64 string to bytes and wrap them as an AES SecretKey
            byte[] decodedKey = Base64.getDecoder().decode(keyBase64);
            SecretKey secretKey = new SecretKeySpec(decodedKey, "AES");

            // Checks that the key length is correct for the 128-bit AES
            if(keyBase64.length() != 24)
            {
                System.out.println("❌ Invalid AES Key length");
            }

            // Read encrypted file
            byte[] fileBytes = Files.readAllBytes(encryptedFile.toPath());

            // Take IV (first 12 bytes)
            byte[] ivBytes = Arrays.copyOfRange(fileBytes, 0, 12);
            GCMParameterSpec ivSpec = new GCMParameterSpec(128, ivBytes);

            // Extract encrypted text
            byte[] cipherText = Arrays.copyOfRange(fileBytes, 12, fileBytes.length);

            //Sets up the cipher for decryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            //Decrypt the content in the file
            byte[] plainText =  cipher.doFinal(cipherText);


            //Saves the decrypted file
            Files.write(Paths.get("plaintext.txt"), plainText);

            //Prints out the decrypted file as plaintext
            System.out.println("\n✅ File decrypted successfully!");
            System.out.println("Decrypted file saved as: plaintext.txt");

        }
        catch (Exception e)
        {
            System.out.println("❌ An Error during decryption: " + e.getMessage());
        }
    }

}