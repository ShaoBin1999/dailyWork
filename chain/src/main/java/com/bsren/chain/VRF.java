package com.bsren.chain;


import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

import java.math.BigInteger;
import java.security.SecureRandom;

public class VRF {

    public static void main(String[] args) throws Exception {
        // Generate a VRF key pair
        Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(new SecureRandom());
        Ed25519PublicKeyParameters publicKey = privateKey.generatePublicKey();

        // Choose a message and a domain
        byte[] message = "Hello, world!".getBytes();
        byte[] domain = "example.com".getBytes();

        // Compute the VRF output
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, privateKey);
        signer.update(message, 0, message.length);
//        signer.update(domain, 0, domain.length);
        byte[] vrfOutput = signer.generateSignature();

        // Verify the VRF output
        signer.init(false, publicKey);
        signer.update(vrfOutput, 0, vrfOutput.length);
        if (!signer.verifySignature(vrfOutput)) {
            throw new Exception("VRF output not verified");
        }

        // Extract the random value from the VRF output
        byte[] randomValueBytes = new byte[16];
        System.arraycopy(vrfOutput, 32, randomValueBytes, 0, 16);
        BigInteger randomValueInt = new BigInteger(1, randomValueBytes);

        // Generate a 128-bit random value using the random value bytes
        SecureRandom random = new SecureRandom(randomValueBytes);
        long randomValue = random.nextLong();

        System.out.println("Random value: " + randomValue);
    }
}
