package com.example.jobapp_u.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.regex.Pattern



class InputValidation {
    companion object {
        private val EMAIL_ADDRESS_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$")

        /**
         * 1. Password should contain at least 4 characters
         * 2. Password should contain at least one letter
         * 3. Password should contain at least one digit
         * 4. Password should contain at least one special character from the specified set of characters
         * Pattern.compile("^.*(?=.{4,})(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!&\$%&?#_@ ]).*\$") // new pattern
         */
        private val PASSWORD_PATTERN =
            Pattern.compile("^.*(?=.{4,})(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!&\$%&?#_@ ]).*\$")


        fun checkNullity(input: String): Boolean {
            return input.isNotEmpty()
        }


        fun isUsernameValid(username: String): Pair<Boolean, String> {
            if (username.isEmpty()) {
                return Pair(false, "Username cannot be empty.")
            }
            if (username.length > 100) {
                return Pair(false, "Username cannot be more than 100 characters.")
            }
            if (username[0].isDigit()) {
                return Pair(false, "Username cannot start with a number.")
            }
            if (username.matches("^[a-zA-Z0-9 ]+$".toRegex()).not()) {
                return Pair(false, "Username can only contain alphabets and numbers.")
            }
            return Pair(true, "")
        }


        fun isEmailValid(email: String): Pair<Boolean, String> {
            if (email.isEmpty()) {
                return Pair(false, "Email cannot be empty.")
            }
            if (email[0].isDigit()) {
                return Pair(false, "Email cannot start with a number.")
            }
            if (EMAIL_ADDRESS_PATTERN.matcher(email).matches().not()) {
                return Pair(false, "Email is not valid.")
            }
            return Pair(true, "")
        }


        fun isPasswordValid(password: String): Pair<Boolean, String> {
            if (password.isEmpty()) {
                return Pair(false, "Password cannot be empty.")
            }
            if (PASSWORD_PATTERN.matcher(password).matches().not()) {
                return Pair(false, "Password is not valid.")
            }
            return Pair(true, "")
        }


        fun isSapIdValid(sapId: String): Pair<Boolean, String> {
            if (sapId.isEmpty()) {
                return Pair(false, "SAP ID cannot be empty.")
            }
            if (sapId.length != 11) {
                return Pair(false, "SAP ID must be 11 characters.")
            }
            if (!sapId.matches("^[0-9]+$".toRegex())) {
                return Pair(false, "SAP ID can only contain digits.")
            }
            return Pair(true, "")
        }


        fun isMobileNumberValid(mobileNumber: String): Pair<Boolean, String> {
            if (mobileNumber.isEmpty()) {
                return Pair(false, "Mobile number cannot be empty.")
            }
            if (mobileNumber.length != 10) {
                return Pair(false, "Mobile number must be 10 characters.")
            }
            if (mobileNumber.substring(0, 3).equals("000")) {
                return Pair(false, "Mobile number cannot start with 000.")
            }
            val firstDigit = mobileNumber[0]
            if (mobileNumber.slice(0..9).all { it == firstDigit }) {
                return Pair(false, "All the digits in mobile number cannot be same.")
            }
            if (mobileNumber.matches("^[0-9]+$".toRegex()).not()) {
                return Pair(false, "Mobile number can only contain digits.")
            }
            return Pair(true, "")
        }



        @RequiresApi(Build.VERSION_CODES.O)
        fun isDOBValid(dob: String): Pair<Boolean, String> {
            if (dob.isEmpty()) {
                return Pair(false, "Date of Birth cannot be empty.")
            }
            val dobDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val now = LocalDate.now()
            val age = ChronoUnit.YEARS.between(dobDate, now)
            if (age < 18) {
                return Pair(false, "Age must be 18 years or older.")
            }
            return Pair(true, "")
        }


        fun genderValidation(gender: String): Boolean {
            return checkNullity(gender)
        }


        fun isAddressValid(address: String): Pair<Boolean, String> {
            if (address.isEmpty()) {
                return Pair(false, "Address cannot be empty.")
            }
            if (address.length > 200) {
                return Pair(false, "Address cannot be more than 200 characters.")
            }
            if (address.matches("^[a-zA-Z0-9,-/ ]+$".toRegex()).not()) {
                return Pair(false, "Address is not valid.")
            }
            return Pair(true, "")
        }


        fun isCityValid(city: String): Pair<Boolean, String> {
            if (city.isEmpty()) {
                return Pair(false, "City cannot be empty.")
            }
            if (!city.matches("^[a-zA-Z ]+$".toRegex())) {
                return Pair(false, "City can only contain letters and spaces.")
            }
            if (city.length > 100) {
                return Pair(false, "City cannot be more than 100 characters.")
            }
            return Pair(true, "")
        }


        fun isStateValid(state: String): Pair<Boolean, String> {
            if (state.isEmpty()) {
                return Pair(false, "State cannot be empty.")
            }
            if (!state.matches("^[a-zA-Z ]+$".toRegex())) {
                return Pair(false, "State can only contain letters and spaces.")
            }
            if (state.length > 100) {
                return Pair(false, "State cannot be more than 100 characters.")
            }
            return Pair(true, "")
        }

        fun isZipCodeValid(zipCode: String): Pair<Boolean, String> {
            if (zipCode.isEmpty()) {
                return Pair(false, "Zip code cannot be empty.")
            }
            if (!zipCode.matches("^[0-9]+$".toRegex())) {
                return Pair(false, "Zip code can only contain digits.")
            }
            if (zipCode.length != 6) {
                return Pair(false, "Zip code must be 6 characters.")
            }
            return Pair(true, "")
        }


        fun isScoreValid(score: String): Pair<Boolean, String> {
            if (score.isEmpty()) {
                return Pair(false, "Score cannot be empty.")
            }

            if (score.matches("^-?[0-9]*\\.?[0-9]+$".toRegex()).not()) {
                return Pair(false, "Score can only contain digits.")
            }

            if (score.startsWith("00")) {
                return Pair(false, "Score cannot start with 00.")
            }

            val scoreValue = score.toDouble()
            if (scoreValue <= 0.0) {
                return Pair(false, "Score cannot be negative.")
            }

            if (scoreValue > 10.0) {
                return Pair(false, "Score cannot be more than 10.")
            }
            return Pair(true, "")
        }
    }
}