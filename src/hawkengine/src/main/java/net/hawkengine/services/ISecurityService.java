//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

public interface ISecurityService {
	String encrypt(String plainText);

	String decrypt(String cipherText);

}
