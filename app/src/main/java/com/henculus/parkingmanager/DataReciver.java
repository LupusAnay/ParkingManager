package com.henculus.parkingmanager;


import android.net.Uri;
import android.os.Bundle;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Класс для работы с API сервера
 */
public class DataReciver {
    //Запросы к БД

    public final static String USERS_URI = "http://172.20.47.2/user7/auth.php";
    public final static String SELECT_USERS = "1";
    public final static String INSERT_USERS = "2";
    public final static String UPDATE_USERS = "3";
    public final static String DELETE_USERS = "4";

    //Класс клиентов
    public static class Users {
        // private int uId;
        private String uLogin;
        private String uPasswod;
        private String uSurname;
        private String uName;
        private String uPatronymic;
        private String uPhoneNumber;
        private String uEmail;
        private String uAdress;
        private String uPassport;

        public Users() {
            //   uId = 0;
            uLogin = "";
            uPasswod = "";
            uSurname = "";
            uName = "";
            uPatronymic = "";
            uPhoneNumber = "";
            uEmail = "";
            uAdress = "";
            uPassport = "";
        }

      /*  public int getuId() {
            return uId;
        }

        public void setuId(int id_users) {
            uId = id_users;
        }*/

        public String getuLogin() {
            return uLogin;
        }

        public void setuLogin(String login) {
            uLogin = login;
        }

        public String getuPasswod() {
            return uPasswod;
        }

        public void setuPasswod(String password) {
            uPasswod = password;
        }

        public String getuSurname() {
            return uSurname;
        }

        public void setuSurname(String surname) {
            uSurname = surname;
        }

        public String getuName() {
            return uName;
        }

        public void setuName(String name) {
            uName = name;
        }

        public String getuPatronymic() {
            return uPatronymic;
        }

        public void setuPatronymic(String patronymic) {
            uPatronymic = patronymic;
        }

        public String getuPhoneNumber() {
            return uPhoneNumber;
        }

        public void setuPhoneNumber(String phonenumber) {
            uPhoneNumber = phonenumber;
        }

        public String getuAdress() {
            return uAdress;
        }

        public void setuAdress(String adress) {
            uAdress = adress;
        }

        public String getuEmail() {
            return uEmail;
        }

        public void setuEmail(String email) {
            uEmail = email;
        }

        public String getuPassport() {
            return uPassport;
        }

        public void setuPassport(String passport) {
            uPassport = passport;
        }

        @Override
        public String toString() {
            return ("Users [uLogin=" + uLogin + ", uPassword=" + uPasswod + ", uSurname=" + uSurname + ", uName=" + uName + "" +
                    ", uPatronymic=" + uPatronymic + ", PHONE_NUMDER=" + uPhoneNumber +"]");
        }
    }



    public static int writeUsers(String login, String password, String cmd, Bundle values) throws IOException {
        ArrayList<Users> agents = new ArrayList<>();
        JSONArray arrayObj = null;
        int result = 0;

        URL url = null;
        BufferedReader reader = null;
        String s = null;
        try {
            url = new URL(USERS_URI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("COMPANY_NAME", login)
                    .appendQueryParameter("ADRESS", password)
                            //.appendQueryParameter("cmd", cmd)
                            //.appendQueryParameter("id_users", String.valueOf(values.getInt("id_users", 0)))
                    .appendQueryParameter("CUSTOMER_NAME", values.getString("CREDIT_LIMIT", ""))
                    .appendQueryParameter("CITY", values.getString("CUSTOMER_NAME", ""))
                    .appendQueryParameter("COUNTRY", values.getString("CITY", ""))
                    .appendQueryParameter("PHONE_NUMDER", values.getString("PHONE_NUMDER", ""))
                    .appendQueryParameter("CREDIT_LIMIT", values.getString("COUNTRY", ""));

            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder buf = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            s = buf.toString();
            if (s.equals("0") || s.trim().equals("")) {
                return result;
            }
            else  {
                result = Integer.valueOf(s);
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return 0;
        }
        catch (SocketTimeoutException e){
            e.printStackTrace();
            return 0;
        }
        catch (ConnectException e) {
            e.printStackTrace();
            return 0;
        }
        finally {
            if (reader != null)
                reader.close();
            return result;
        }
    }
}
