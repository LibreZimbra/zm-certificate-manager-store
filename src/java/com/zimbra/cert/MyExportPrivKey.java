/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013, 2014, 2016 Synacor, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

public class MyExportPrivKey {
        private File keystoreFile;
        private String keyStoreType;
        private char[] password;
        private String alias;
        private File exportedFile;

        public static KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
                try {
                        Key key=keystore.getKey(alias,password);
                        if(key instanceof PrivateKey) {
                                Certificate cert=keystore.getCertificate(alias);
                                PublicKey publicKey=cert.getPublicKey();
                                return new KeyPair(publicKey,(PrivateKey)key);
                        }
                } catch (UnrecoverableKeyException e) { return null;
        } catch (NoSuchAlgorithmException e) { return null;
        } catch (KeyStoreException e) { return null;
        }
        return null;
        }

        public void export() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
            KeyStore keystore=KeyStore.getInstance(keyStoreType);
            FileInputStream input = new FileInputStream(keystoreFile);
            keystore.load(input,password);
            KeyPair keyPair=getPrivateKey(keystore,alias,password);
            PrivateKey privateKey=keyPair.getPrivate();
            FileWriter fw=new FileWriter(exportedFile);
            fw.write("-----BEGIN PRIVATE KEY-----\n");
            fw.write(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            fw.write("\n");
            fw.write("-----END PRIVATE KEY-----");
            fw.close();
            input.close();
        }

        /**
         * See http://www.anandsekar.com/2006/01/19/exporting-the-private-key-from-a-jks-keystore/
         * java -jar ExportPrivateKey.zip {keystore_path} JKS {keystore_password} {alias} {target_file}
         * This would export the key to PKCS #8 PEM format.
         * Then you need to run "openssl pkcs8 -inform PEM -nocrypt -in exported-pkcs8.key -out exported.key" afterwards
         * to convert it to the format of zimbra server.key (it is also apache modssl expects)
         * @param args
         * @throws Exception
         * 
         */
        public static void main(String args[]) throws Exception{
               MyExportPrivKey export=new MyExportPrivKey();
               export.keystoreFile=new File(args[0]);
               export.keyStoreType=args[1];
               export.password=args[2].toCharArray();
               export.alias=args[3];
               export.exportedFile=new File(args[4]);
               export.export();
        }
}
