package com.example.emrtdapplication.utils;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyParameters;
import org.spongycastle.crypto.util.PublicKeyFactory;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.NamedParameterSpec;
import java.security.spec.X509EncodedKeySpec;

public class ECPublicKey implements ECKey, java.security.interfaces.ECPublicKey {
    private final SubjectPublicKeyInfo info;
    private final ECParameterSpec params;
    private final ECPoint W;

    ECPublicKey(SubjectPublicKeyInfo info, X509EncodedKeySpec spec) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidParameterSpecException {
        this.info = info;
        ECKeyParameters ec = (ECKeyParameters) PublicKeyFactory.createKey(info.getEncoded());
        ec.getParameters().getCurve().getField().getDimension();
        ECField field = new ECFieldFp(ec.getParameters().getCurve().getField().getCharacteristic());
        EllipticCurve curve = new EllipticCurve(field, ec.getParameters().getCurve().getA().toBigInteger(), ec.getParameters().getCurve().getB().toBigInteger());
        ECPoint point = new ECPoint(ec.getParameters().getG().getAffineXCoord().toBigInteger(), ec.getParameters().getG().getAffineYCoord().toBigInteger());
        //params = new ECParameterSpec(curve, point, ec.getParameters().getN(), ec.getParameters().getH().intValue());
        ECGenParameterSpec bp = new ECGenParameterSpec("brainpoolP384r1");
        AlgorithmParameters alg = AlgorithmParameters.getInstance(info.getAlgorithm().getAlgorithm().getId());
        alg.init(bp);
        params = alg.getParameterSpec(ECParameterSpec.class);
        org.spongycastle.math.ec.ECPoint pub = ec.getParameters().getCurve().decodePoint(info.getPublicKeyData().getBytes());
        W = new ECPoint(pub.getAffineXCoord().toBigInteger(), pub.getAffineYCoord().toBigInteger());
    }

    @Override
    public String getAlgorithm() {
        return "EC";
    }

    @Override
    public byte[] getEncoded() {
        try {
            return info.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public ECPoint getW() {
        return W;
    }

    @Override
    public ECParameterSpec getParams() {
        return params;
    }
}
