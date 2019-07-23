package org.enterprisedlt.fabric.service.node.cryptography

import java.io.{File, FileWriter}
import java.time.{LocalDate, ZoneOffset}
import java.util.Date

import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.enterprisedlt.fabric.service.node.configuration.OrganizationConfig

/**
  * @author Alexey Polubelov
  */
object FabricCryptoMaterial {

    def generateOrgCrypto(orgConfig: OrganizationConfig, orgFullName: String, path: String, components: Array[FabricComponent]): Unit = {
        //    CA
        val caCert = FabricCryptoMaterial.generateCACert(
            organization = orgFullName,
            location = orgConfig.location,
            state = orgConfig.state,
            country = orgConfig.country
        )
        val caDir = s"$path/ca"
        mkDir(caDir)
        writeToPemFile(s"$caDir/ca.$orgFullName-cert.pem", caCert.certificate)
        writeToPemFile(s"$caDir/ca_sk", caCert.key)

        //    TLS CA
        val tlscaCert = FabricCryptoMaterial.generateTLSCACert(
            organization = orgFullName,
            location = orgConfig.location,
            state = orgConfig.state,
            country = orgConfig.country
        )
        val tlscaDir = s"$path/tlsca"
        mkDir(tlscaDir)
        writeToPemFile(s"$tlscaDir/tlsca.$orgFullName-cert.pem", tlscaCert.certificate)
        writeToPemFile(s"$tlscaDir/tlsca_sk", tlscaCert.key)

        //    Admin
        val adminCert = FabricCryptoMaterial.generateAdminCert(
            organization = orgFullName,
            location = orgConfig.location,
            state = orgConfig.state,
            country = orgConfig.country,
            caCert
        )
        val adminDir = s"$path/users/Admin@$orgFullName/msp"
        mkDir(s"$adminDir/admincerts")
        writeToPemFile(s"$adminDir/admincerts/Admin@$orgFullName-cert.pem", adminCert.certificate)

        mkDir(s"$adminDir/cacerts")
        writeToPemFile(s"$adminDir/cacerts/ca.$orgFullName-cert.pem", caCert.certificate)

        mkDir(s"$adminDir/keystore")
        writeToPemFile(s"$adminDir/keystore/admin_sk", adminCert.key)

        mkDir(s"$adminDir/signcerts")
        writeToPemFile(s"$adminDir/signcerts/Admin@$orgFullName-cert.pem", adminCert.certificate)

        mkDir(s"$adminDir/tlscacerts")
        writeToPemFile(s"$adminDir/tlscacerts/tlsca.$orgFullName-cert.pem", tlscaCert.certificate)

        // MSP
        val mspDir = s"$path/msp"
        mkDir(s"$mspDir/admincerts")
        writeToPemFile(s"$mspDir/admincerts/Admin@$orgFullName-cert.pem", adminCert.certificate)

        mkDir(s"$mspDir/cacerts")
        writeToPemFile(s"$mspDir/cacerts/ca.$orgFullName-cert.pem", caCert.certificate)

        mkDir(s"$mspDir/tlscacerts")
        writeToPemFile(s"$mspDir/tlscacerts/tlsca.$orgFullName-cert.pem", tlscaCert.certificate)

        components.foreach { component =>
            createComponentDir(orgConfig, orgFullName, component, path, caCert, tlscaCert, adminCert)
        }
    }

    private def createComponentDir(
        orgConfig: OrganizationConfig,
        orgFullName: String,
        component: FabricComponent,
        path: String,
        caCert: CertAndKey,
        tlscaCert: CertAndKey,
        adminCert: CertAndKey): Unit = {
        val outPath = s"$path/${component.group}/${component.name}.$orgFullName"
        mkDir(s"$outPath/msp/admincerts")
        writeToPemFile(s"$outPath/msp/admincerts/Admin@$orgFullName-cert.pem", adminCert.certificate)

        mkDir(s"$outPath/msp/cacerts")
        writeToPemFile(s"$outPath/msp/cacerts/ca.$orgFullName-cert.pem", caCert.certificate)

        mkDir(s"$outPath/msp/tlscacerts")
        writeToPemFile(s"$outPath/msp/tlscacerts/tlsca.$orgFullName-cert.pem", tlscaCert.certificate)

        val theCert = FabricCryptoMaterial.generateComponentCert(
            componentName = component.name,
            organizationUnit = component.unit,
            organization = orgFullName,
            location = orgConfig.location,
            state = orgConfig.state,
            country = orgConfig.country,
            caCert
        )
        mkDir(s"$outPath/msp/keystore")
        writeToPemFile(s"$outPath/msp/keystore/${component}_sk", theCert.key)

        mkDir(s"$outPath/msp/signcerts")
        writeToPemFile(s"$outPath/msp/signcerts/$component.$orgFullName-cert.pem", theCert.certificate)

        val tlsCert = FabricCryptoMaterial.generateComponentTlsCert(
            componentName = component.name,
            organization = orgFullName,
            location = orgConfig.location,
            state = orgConfig.state,
            country = orgConfig.country,
            tlscaCert
        )
        mkDir(s"$outPath/tls")
        writeToPemFile(s"$outPath/tls/ca.crt", tlscaCert.certificate)
        writeToPemFile(s"$outPath/tls/server.crt", tlsCert.certificate)
        writeToPemFile(s"$outPath/tls/server.key", tlsCert.key)
    }


    private def writeToPemFile(fileName: String, o: AnyRef): Unit = {
        val writer = new JcaPEMWriter(new FileWriter(fileName))
        writer.writeObject(o)
        writer.close()
    }

    private def mkDir(path: String): Boolean = new File(path).mkdirs()

    private def generateCACert(
        organization: String,
        location: String,
        state: String,
        country: String
    ): CertAndKey = {
        CryptoUtil.createSelfSignedCert(
            OrgMeta(
                name = s"ca.$organization",
                organization = Option(organization),
                location = Option(location),
                state = Option(state),
                country = Option(country)
            ),
            Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Array(
                CertForCA,
                UseForDigitalSignature,
                UseForEncipherment,
                UseForCertSign,
                UseForCRLSign,
                UseForClientAuth,
                UseForServerAuth
            )
        )
    }

    private def generateTLSCACert(
        organization: String,
        location: String,
        state: String,
        country: String
    ): CertAndKey = {
        CryptoUtil.createSelfSignedCert(
            OrgMeta(
                name = s"tlsca.$organization",
                organization = Option(organization),
                location = Option(location),
                state = Option(state),
                country = Option(country)
            ),
            Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Array(
                CertForCA,
                UseForDigitalSignature,
                UseForEncipherment,
                UseForCertSign,
                UseForCRLSign,
                UseForClientAuth,
                UseForServerAuth
            )
        )
    }

    private def generateAdminCert(
        organization: String,
        location: String,
        state: String,
        country: String,
        signCert: CertAndKey
    ): CertAndKey = {
        CryptoUtil.createSignedCert(
            OrgMeta(
                name = s"Admin@$organization",
                organizationUnit = Option("client"),
                location = Option(location),
                state = Option(state),
                country = Option(country),
            ),
            Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Array(
                CertNotForCA,
                UseForDigitalSignature
            ),
            signCert
        )
    }

    private def generateComponentCert(
        componentName: String,
        organizationUnit: Option[String],
        organization: String,
        location: String,
        state: String,
        country: String,
        signCert: CertAndKey
    ): CertAndKey = {
        CryptoUtil.createSignedCert(
            OrgMeta(
                name = s"$componentName.$organization",
                organizationUnit = organizationUnit,
                location = Option(location),
                state = Option(state),
                country = Option(country)
            ),
            Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Array(
                CertNotForCA,
                UseForDigitalSignature
            ),
            signCert
        )
    }

    private def generateComponentTlsCert(
        componentName: String,
        organization: String,
        location: String,
        state: String,
        country: String,
        signCert: CertAndKey
    ): CertAndKey = {
        CryptoUtil.createSignedCert(
            OrgMeta(
                name = s"$componentName.$organization",
                location = Option(location),
                state = Option(state),
                country = Option(country)
            ),
            Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant),
            Array(
                CertNotForCA,
                UseForDigitalSignature,
                UseForEncipherment,
                UseForClientAuth,
                UseForServerAuth,
                AlternativeDNSName(s"$componentName.$organization"),
                AlternativeDNSName(componentName),
            ),
            signCert
        )
    }

}

case class FabricComponent(
    group: String,
    name: String,
    unit: Option[String] = None
)