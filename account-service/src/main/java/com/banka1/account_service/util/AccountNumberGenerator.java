package com.banka1.account_service.util;

import com.banka1.account_service.repository.AccountRepository;

import java.util.Random;

/**
 * Utility klasa za generisanje i validaciju 18-cifrenih bankovskih brojeva racuna.
 * <p>
 * <strong>Struktura broja racuna:</strong>
 * <pre>
 *   Pozicije 1–3    : Kod banke (fiksno: 111)
 *   Pozicije 4–7    : Kod fijale (fiksno: 0001)
 *   Pozicije 8–16   : 9 nasumicnih cifara
 *   Pozicije 17–18  : Kod tipa racuna (2 cifre, npr. 11 za licni tekuci, 21 za poslovni)
 *   Pozicija  19    : Kontrolna cifra (modulo 11 algoritam)
 * </pre>
 * <p>
 * Ova klasa je immutable i ne sadrzi stanje.
 */
public final class AccountNumberGenerator {

    private AccountNumberGenerator() {}

    /**
     * Izracunava kontrolnu cifru za prefix broja racuna koristeći modulo 11 algoritam.
     * <p>
     * Vraća vrednost 0–9. Ako je rezultat 10, to je nevažeći (pozivajući kod mora ponovo pokušati).
     *
     * @param prefix prvo 17 cifara broja racuna
     * @return kontrolna cifra (0–9) ili 10 ako je nevažeća (zahteva retry)
     */
    public static int calculateCheckDigit(String prefix) {
        int sum = 0;
        for (char c : prefix.toCharArray()) {
            sum += c - '0';
        }
        return (11 - sum % 11) % 11;
    }

    /**
     * Validira 18-cifreni broj racuna proveravanjem kontrolne cifre i formata.
     * <p>
     * Broj mora:
     * <ul>
     *   <li>Sadrzavati tacno 18 cifara</li>
     *   <li>Sadrzavati samo numeričke znakove</li>
     *   <li>Imati ispravnu kontrolnu cifru (nije 10)</li>
     * </ul>
     *
     * @param number broj racuna za validaciju
     * @return {@code true} ako je broj validan, {@code false} inače
     */
    public static boolean validateAccountNumber(String number) {
        if (number == null || number.length() != 18) return false;
        for (char c : number.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        String prefix = number.substring(0, 17);
        int expected = calculateCheckDigit(prefix);
        if (expected == 10) return false;
        return (number.charAt(17) - '0') == expected;
    }

    /**
     * Generiše jedinstveni 18-cifreni broj racuna.
     * <p>
     * Proces:
     * <ol>
     *   <li>Genera se random deo od 9 cifara</li>
     *   <li>Gradi se broj sa fiksnim kodom banke (111), fijale (0001), random delom i tipom racuna</li>
     *   <li>Izracuna se kontrolna cifra</li>
     *   <li>Ako kontrolna cifra bude 10, proces se ponavlja</li>
     *   <li>Ako broj vec postoji u bazi, proces se ponavlja</li>
     *   <li>Kada je broj validan i jedinstven, vraća se</li>
     * </ol>
     *
     * @param typeVal kod tipa racuna kao string (npr. "11" za licni tekuci, "21" za poslovni)
     * @param random instanca {@link Random} klase za generisanje nasumičnih cifara
     * @param accountRepository repository za proveru jedinstvnosti broja u bazi
     * @return jedinstveni, validan 18-cifreni broj racuna
     */
    public static String generate(String typeVal, Random random, AccountRepository accountRepository) {
        StringBuilder sb = new StringBuilder();
        String val = "";
        boolean exists = true;
        while (exists) {
            sb.setLength(0);
            for (int i = 0; i < 9; i++) {
                sb.append(random.nextInt(10));
            }
            val = "111" + "0001" + sb + typeVal;
            int checkDigit = calculateCheckDigit(val);
            if (checkDigit == 10) continue;
            val += checkDigit;
            exists = accountRepository.existsByBrojRacuna(val);
        }
        return val;
    }
}
