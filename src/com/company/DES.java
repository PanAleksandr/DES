package com.company;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DES {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Input fields for original text and secret key
            System.out.print("Enter the original text: ");
            String originalText = scanner.nextLine();

            System.out.print("Enter the secret key (8 characters): ");
            String key = scanner.nextLine();

            if (key.length() != 8) {  //tik 8 simbol gali buti
                System.out.println("Error: Secret key must be exactly 8 characters long.");
                return;
            }

            // Choice of function encryption/decryption
            System.out.print("Choose encryption (E) or decryption (D): ");
            String choice = scanner.nextLine();

            // Choice of encryption mode
            System.out.print("Choose encryption mode (ECB, CBC, CFB): ");
            String mode = scanner.nextLine();

            // DES encryption/decryption
            byte[] result;
            if (choice.equalsIgnoreCase("E")) {
                result = encrypt(originalText, key, mode);
                System.out.println("Encrypted text: " + bytesToHex(result));//result

                // Option to save encrypted text to a file
                System.out.print("Do you want to save the encrypted text to a file? (Yes/No): ");
                String saveChoice = scanner.nextLine();//pasirinkimas
                if (saveChoice.equalsIgnoreCase("Yes")) {
                    System.out.print("Enter the file path to save encrypted text: ");
                    String saveFilePath = scanner.nextLine();//nuskaito failo kelia
                    saveToFile(result, saveFilePath);//text encrypt->File
                    System.out.println("Encrypted text successfully saved to file.");
                }
            } else if (choice.equalsIgnoreCase("D")) {
                // Reading ciphertext from a file for decryption
                System.out.print("Enter the file path to read ciphertext from: ");
                String filePath = scanner.nextLine();//nuskaito failo kelia
                byte[] encryptedData = readFromFile(filePath);//skaito kas ten faile
                if (encryptedData != null) {
                    result = decrypt(encryptedData, key, mode);
                    if (result != null) {
                        System.out.println("Decrypted text: " + new String(result, StandardCharsets.UTF_8));//decrypted
                    } else {
                        System.out.println("Error: Failed to decrypt the ciphertext.");
                    }
                } else {
                    System.out.println("Error: Failed to read ciphertext from the file.");
                }
            } else {
                System.out.println("Invalid choice. Program exits.");
            }
        } catch (Exception e) { //isimtis
            e.printStackTrace();
        }
    }
//en
    public static byte[] encrypt(String originalText, String key, String mode) throws Exception {//// Šifruoja originalų tekstą (throws) Išmeta išimtį, jei šifravimo metu įvyksta klaida.

        Cipher cipher = Cipher.getInstance("DES/" + mode + "/PKCS5Padding");//Sukuria šifravimo objektą šifravimui.PKCS5Padding naudojamas duomenų vientisumui ir saugumui užtikrinti, kai šifruojamas DES režimu.
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "DES");//// Konvertuoja slaptąjį raktą į SecretKeySpec objektą.//StandardCharsets.UTF_8 naudojamas slaptam raktui konvertuoti iš eilutės į baitų seką.
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);//// Inicijuoja šifrą su slaptu šifravimo raktu.
        return cipher.doFinal(originalText.getBytes(StandardCharsets.UTF_8));//// Šifruoja pradinį tekstą ir grąžina užšifruotus baitus.
    }

    public static byte[] decrypt(byte[] ciphertext, String key, String mode) {
        try {
            Cipher cipher = Cipher.getInstance("DES/" + mode + "/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // Funkcija bytesToHex konvertuoja baitų masyvą į šešioliktainę eilutę.
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {                          //kartojasi per kiekvieną masyvo baitą
            result.append(String.format("%02X", b)); //Kiekvienas baitas formatuojamas į dviejų skaitmenų šešioliktainį skaičių naudojant šį metodą
        }
        return result.toString();
    }

    public static void saveToFile(byte[] data, String filename) {
        try (FileOutputStream outputStream = new FileOutputStream(filename)) {  //sukuriamas naujas FileOutputStream objektas ir naudojamas duomenims įrašyti į failą.
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readFromFile(String filePath) {             //kuris paima failo kelią kaip parametrą ir grąžina baitų masyvą su duomenimis iš to failo.
        try (FileInputStream fis = new FileInputStream(filePath)) { //FileInputStream,naudojamas duomenims iš failo nuskaityti. Perduotas kelias į failą filePath, iš kurio bus nuskaitomi duomenys.
            File file = new File(filePath);                        //su nurodytu failo keliu. Šis objektas naudojamas failo dydžiui nustatyti.
            byte[] data = new byte[(int) file.length()];  //Sukuriamas duomenų baitų masyvas, lygus failo dydžiui.
            fis.read(data); //Duomenys iš failo nuskaitomi į baitų masyvą
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
