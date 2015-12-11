/**
 * 
 */
package com.rocket.tms.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import net.trubiquity.tpa.identity.admin.impl.CommunityAdministrationStub;
import net.trubiquity.tpa.identity.admin.impl.ProfileAdministrationStub;
import net.trubiquity.tpa.identity.impl.IdentityResolverStub;
import net.trubiquity.tpa.identity.impl.IdentityStub;
import net.trubiquity.tpa.identity.impl.KeyData;
import net.trubiquity.tpa.identity.impl.KeyType;
import net.trubiquity.tpa.identity.impl.PasswordCredentials;
import net.trubiquity.tpa.identity.impl.SimpleIdentityToken;
import net.trubiquity.tpa.identity.impl.TRUCoreKeyStore;
import net.trubiquity.tpa.objects.identity.IIdentityToken;
import net.trubiquity.tpa.objects.identity.IMembershipGroupHandle;
import net.trubiquity.tpa.objects.identity.IUser;
import net.trubiquity.tpa.objects.identity.admin.ICommunity.CommunityEncryptionType;
import net.trubiquity.tpa.objects.identity.admin.impl.Community;
import net.trubiquity.tpa.objects.identity.admin.impl.CommunityHandle;
import net.trubiquity.tpa.objects.identity.admin.impl.UserProfile;
import net.trubiquity.tpa.objects.identity.impl.User;
import net.trubiquity.tpa.objects.pkg.IMetaDataList;
import net.trubiquity.tpa.objects.pkg.IPackageList;
import net.trubiquity.tpa.pkg.impl.PackageFilter;
import net.trubiquity.tpa.pkg.impl.PackageHandle;
import net.trubiquity.tpa.pkg.impl.PackageList;
import net.trubiquity.tpa.pkg.impl.PackagePayload;
import net.trubiquity.tpa.pkg.impl.TPAPackage;
import net.trubiquity.tpa.router.ServiceException;
import net.trubiquity.tpa.trushare.impl.TRUShareSimpleClient;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import com.rocket.tms.json.ReqGetDownloadFilsJsonObj;
import com.rocket.tms.json.ReqGetPartnersJsonObj;
import com.rocket.tms.json.ReqLoginJsonObj;
import com.rocket.tms.json.ResFileUploadJsonObj;
import com.rocket.tms.json.RespDownloadFileJsonObj;
import com.rocket.tms.json.RespGetDownloadFilesJsonObj;
import com.rocket.tms.json.RespGetPartnerJsonObj;
import com.rocket.tms.json.RespLoginJsonObj;
import com.rocket.tms.json.RespPartner;
import com.rocket.tms.json.ResponseJsonObj;
import com.rocket.tms.util.Constant;
import com.rocket.tms.util.JsonHelper;
import com.rocket.tpa.mfx.DataExchangeProgressEventArgs;
import com.rocket.tpa.mfx.IMFXClient;
import com.rocket.tpa.mfx.MFXActionListener;
import com.rocket.tpa.mfx.MFXClient;
import com.rocket.tpa.mfx.PackageDetails;
import com.rocket.tpa.mfx.PackageFilterType;

/**
 * @author xxu
 *
 */
@Service
public class TrucoreMSServiceImpl implements TrucoreMSService {

	public static HashMap<String, SimpleIdentityToken> tokenMap = new HashMap<String, SimpleIdentityToken>();
	private CommunityAdministrationStub mCommunityAdministrationStub = new CommunityAdministrationStub(Constant.COMMUNITY_BASE_URL);
	private TRUShareSimpleClient mTRUShareSimpleClient = new TRUShareSimpleClient(Constant.TRUSHARE_IDENTITY_URL, Constant.TRUSHARE_BASE_URL);
	private IdentityResolverStub mIdentityResolverStub = new IdentityResolverStub(Constant.IDENTITY_BASE_URL);
	private ProfileAdministrationStub mProfileAdministrationStub = new ProfileAdministrationStub(Constant.PROFILE_BASE_URL);
	private TRUCoreKeyStore keyStore = new TRUCoreKeyStore(Constant.TRUCORE_STORE_BASE_URL);
	private static String IOStoken = "4884adef5c5dfbb51f4f51807a84d83ae08ac95032c0559dac8b285a49453392";
	
	/**
	 * IMFXClient is used for file transfer to TRUCore server MFXActionListener
	 * is a handler for get proccess status when file transfer , you can
	 * implement yourself's logic on every status.
	 */
	private IMFXClient MFXClient = new MFXClient(Constant.MFX_BASE_URL, new MFXActionListener() {

		@Override
		public void onProgressChanged(DataExchangeProgressEventArgs e) {
			switch (e.Action) {
			case Downloading:
				break;
			case DownloadCompleted:
				break;
			case Decrypting:
				break;
			case DecryptionCompleted:
				break;
			case Encrypting:
				break;
			case EncryptionCompleted:
				break;
			case UploadCompleted:
				break;
			case Uploading:
				String msg = "You receive a new file notification.";
				System.out.println(msg);
				pushNotificationsToiOSdevices(msg);
				break;
			default:
				break;
			}

		}

	}, mIdentityResolverStub, mCommunityAdministrationStub, mTRUShareSimpleClient, mProfileAdministrationStub, keyStore);

    
    
	@Override
	public ResponseJsonObj login(String jsonString) {
		RespLoginJsonObj respLoginJsonObj = new RespLoginJsonObj();
		try {
			ReqLoginJsonObj loginJson = JsonHelper.parseObject(jsonString, ReqLoginJsonObj.class);
			IdentityStub mIdentityStub = new IdentityStub(Constant.IDENTITY_STUB_URL);
			if (loginJson.username == null || loginJson.username.equals("")) {
				respLoginJsonObj.result = TrucoreMSService.FAILED;
				respLoginJsonObj.log = "User name is null";
			}

			else if (loginJson.password == null || loginJson.password.equals("")) {
				respLoginJsonObj.result = TrucoreMSService.FAILED;
				respLoginJsonObj.log = "Password is null";
			} else {
				PasswordCredentials credentials = new PasswordCredentials(loginJson.username, loginJson.password);
				SimpleIdentityToken token = (SimpleIdentityToken) mIdentityStub.login(credentials);
				respLoginJsonObj.result = SUCCESS;
				respLoginJsonObj.token = token.getAuthenticationToken();
//				tokenMap.clear();
				tokenMap.put(token.getAuthenticationToken(), token);
			}

		} catch (JSONException e) {
			respLoginJsonObj.result = TrucoreMSService.FAILED;
			respLoginJsonObj.log = e.getMessage();
			e.printStackTrace();
		} catch (ServiceException e) {
			respLoginJsonObj.result = TrucoreMSService.FAILED;
			respLoginJsonObj.log = e.getMessage();
			e.printStackTrace();
		}
		return respLoginJsonObj;
	}

	@Override
	public ResponseJsonObj getPartners(String jsonString) {
		RespGetPartnerJsonObj respGetPartnerJsonObj = new RespGetPartnerJsonObj();
		try {
			ReqGetPartnersJsonObj getPartnersJsonObj = JsonHelper.parseObject(jsonString, ReqGetPartnersJsonObj.class);
			SimpleIdentityToken token = tokenMap.get(getPartnersJsonObj.token);
			List<IUser> tp = null;
			ProfileAdministrationStub mProfileAdministrationStub = new ProfileAdministrationStub(Constant.PROFILE_BASE_URL);
			CommunityAdministrationStub mCommunityAdministrationStub = new CommunityAdministrationStub(Constant.COMMUNITY_BASE_URL);
			TRUShareSimpleClient mTRUShareSimpleClient = new TRUShareSimpleClient(Constant.TRUSHARE_IDENTITY_URL, Constant.TRUSHARE_BASE_URL);
			UserProfile myProfile = (UserProfile) mProfileAdministrationStub.getMyProfile(token);
			// Get community
			List<IMembershipGroupHandle> lsCommunities = mCommunityAdministrationStub.getCommunitiesOfUser(token, myProfile);
			Community community = (Community) lsCommunities.get(0);
			tp = mTRUShareSimpleClient.findPartners(token, community.getCommunityUniqueID(), "", "", "", "", "", "", "", "", "", 0, 200);
			for (IUser iuser : tp) {
				User user = (User) iuser;
				RespPartner partner = new RespPartner();
				partner.username = user.getUsername();
				partner.company = user.getCompanyName();
				partner.firstname = user.getFirstName();
				partner.lastname = user.getLastName();
				partner.email = user.getEmail();
				respGetPartnerJsonObj.partners.add(partner);
			}
			respGetPartnerJsonObj.result = SUCCESS;
		} catch (Exception e) {
			respGetPartnerJsonObj.result = TrucoreMSService.FAILED;
			respGetPartnerJsonObj.log = e.getMessage();
			e.printStackTrace();
		}
		return respGetPartnerJsonObj;
	}

	@Override
	public ResponseJsonObj getAvaliableDownloadFiles(String jsonString) {
		// Get the avaliable packages from TRUCore server
		PackageFilter fltr = new PackageFilter();
		// fltr.add(PackageFilterKeys.ALL_COMPANY_PACKAGES, "ALL");
		// fltr.add(PackageFilterKeys.STATUS, "Intransit");
		RespGetDownloadFilesJsonObj RespGetDownloadFilesJsonObj = new RespGetDownloadFilesJsonObj();
		try {
			TRUShareSimpleClient mTRUShareSimpleClient = new TRUShareSimpleClient(Constant.TRUSHARE_IDENTITY_URL, Constant.TRUSHARE_BASE_URL);
			ReqGetDownloadFilsJsonObj ReqGetDownloadFilsJsonObj = JsonHelper.parseObject(jsonString, ReqGetDownloadFilsJsonObj.class);
			SimpleIdentityToken token = tokenMap.get(ReqGetDownloadFilsJsonObj.token);
			IPackageList dt = mTRUShareSimpleClient.getPackages(token, fltr, PackageFilterType.RECEIVED, (long) 1);
			PackageList pl = (PackageList) dt;
			for (TPAPackage pgkhd : pl.list) {
				RespDownloadFileJsonObj downloadFileJsonObj = new RespDownloadFileJsonObj();
				PackageHandle handle = (PackageHandle) pgkhd.getHandle();
				String packageID = handle.toString();
				if (packageID.contains("/"))
					packageID = packageID.substring(packageID.lastIndexOf("/") + 1);
				TPAPackage mTPAPackage = (TPAPackage) mTRUShareSimpleClient.getPackage(token, pgkhd.getHandle());
				for (PackagePayload payload : mTPAPackage.payloads) {
					IMetaDataList metaData = payload.getMetaData();
					for (String label : metaData.getLabels()) {
						// [FileName, FileSize, FilePath]
						if (label.equals("FileName")) {
							downloadFileJsonObj.fileName = metaData.get("FileName");
						}
						if (label.equals("FileSize")) {
							downloadFileJsonObj.fileSize = metaData.get("FileSize");
						}
					}
				}
				downloadFileJsonObj.sender = mTPAPackage.getHeader().getCreator().asString();
				PackageDetails pkgDetail = PackageDetails.getPackageDetails(pgkhd);
				downloadFileJsonObj.status = pkgDetail.PackageStatus;
				System.out.println("   ["+pkgDetail.PackageStatus+"] "+downloadFileJsonObj.fileName+" "+downloadFileJsonObj.fileSize);
				if ("DELIVERED".equals(pkgDetail.PackageStatus)) {
					RespGetDownloadFilesJsonObj.downloadFiles.add(downloadFileJsonObj);
				}
			}
		} catch (Exception e) {
			RespGetDownloadFilesJsonObj.result = TrucoreMSService.FAILED;
			RespGetDownloadFilesJsonObj.log = e.getMessage();
			e.printStackTrace();
		}
		return RespGetDownloadFilesJsonObj;
	}

	@Override
	public ResponseJsonObj fileUpload(String tokenStr, List<File> uploadFiles, String encryFileFoder, String pfirstname, String plastname, String pemail) throws Exception {
		ResFileUploadJsonObj res = new ResFileUploadJsonObj();
		SimpleIdentityToken token = tokenMap.get(tokenStr);
		List<IUser> recipients = findPartners(token, pfirstname, plastname, pemail);
		MFXClient.encryptAndUploadAsync(token, recipients, uploadFiles, encryFileFoder);
		res.result = TrucoreMSService.SUCCESS;
		res.token = token.getAuthenticationToken();
		res.log = "You have successfully uploaded "+uploadFiles.size()+" file(s).";
//		UserProfile loginUser = (UserProfile) mProfileAdministrationStub.getMyProfile(token);
//		String msg = loginUser.firstName + " " + loginUser.lastName+" has uploaded a file "+uploadFiles.get(0).getName()+".";
//		pushNotificationsToiOSdevices(msg);
		return res;
	}
	
	/**
	 * Get partners
	 * 
	 * @param communtiyName
	 * @param supplierCode
	 *            (If this supplier code is empty ,the result will query all of
	 *            partners)
	 * @return
	 * @throws ServiceException
	 */
	public List<IUser> findPartners(IIdentityToken token, String pfirstname, String plastname, String pemail) throws ServiceException {
		UserProfile myProfile = (UserProfile) mProfileAdministrationStub.getMyProfile(token);
		// Get community
		List<IMembershipGroupHandle> lsCommunities = mCommunityAdministrationStub.getCommunitiesOfUser(token, myProfile);
		Community community = (Community) lsCommunities.get(0);
		return mTRUShareSimpleClient.findPartners(token, community.getCommunityUniqueID(), "", "", "", pfirstname, plastname, pemail, "", "", "", 0, 1);
	}
	
	@Override
	public File fileDownload(String tokenStr, String filename,
			String downloadFolder) {
		SimpleIdentityToken token = tokenMap.get(tokenStr);
		// Get the avaliable packages from TRUCore server
		PackageFilter fltr = new PackageFilter();
		// fltr.add(PackageFilterKeys.ALL_COMPANY_PACKAGES, "ALL");
		// fltr.add(PackageFilterKeys.STATUS, "Intransit");
		IPackageList dt;
		try {
			dt = mTRUShareSimpleClient.getPackages(token, fltr, PackageFilterType.RECEIVED, (long) 1);
		
			PackageList pl = (PackageList) dt;
			if (pl.list.size() <= 0) {
				return null;
			}
//			List<String> files = new ArrayList<String>();
			outer:for (TPAPackage pkg : pl.list) {
				PackageHandle handle = (PackageHandle) pkg.getHandle();
				String packageID = handle.toString();
				if (packageID.contains("/"))
					packageID = packageID.substring(packageID.lastIndexOf("/") + 1);
				TPAPackage mTPAPackage = (TPAPackage) mTRUShareSimpleClient.getPackage(token, pkg.getHandle());
				for (PackagePayload payload : mTPAPackage.payloads) {
					IMetaDataList metaData = payload.getMetaData();
					for (String label : metaData.getLabels()) {
						if (label.equals("FileName")) {
							String fName = metaData.get("FileName");
							if (filename.equals(fName)) {
//								files.add(metaData.get(IMFXClient.FILE_PATH));
//								// get my private key for file decryption
//								// get package details and ASYNC call server I have done to download
//								// request
//								PackageDetails packageDetails = PackageDetails.getPackageDetails(mTPAPackage);
//								CommunityHandle communityHandle = new CommunityHandle(mCommunityAdministrationStub.handler.getBaseUrl() + "community/" + packageDetails.CommunityID, packageDetails.CommunityID);
//								// CommunityHandle communityHandle = new CommunityHandle("",
//								// packageDetails.CommunityID);
//								Community community = (Community) mIdentityResolverStub.getCommunity(token, communityHandle);// get
//								// communites
//								// details
//								UserProfile recipient = (UserProfile) mProfileAdministrationStub.getMyProfile(token);
//								KeyData recipientPrivateKeyInfo = new KeyData();
//								recipientPrivateKeyInfo.setKeyType(KeyType.UserPrivateKeyOnServer);
//								recipientPrivateKeyInfo.setUserHandle(recipient);
//								if (community.getEncryptionType() == CommunityEncryptionType.CommunityKeyEncryption) {
//									recipientPrivateKeyInfo.setCommunityHandle(communityHandle);
//									recipientPrivateKeyInfo.setKeyType(KeyType.Community);
//								}
								
//								MFXClient.downloadAndDecryptFiles(token, keyStore, recipientPrivateKeyInfo, packageID, files, downloadFolder + Constant.DOWNLOAD_FOLDER, true);
								List<TPAPackage> pkgList = new ArrayList<TPAPackage>();
								pkgList.add(pkg);
								MFXClient.downloadAdnDecryptFiles(token, pkgList, downloadFolder + Constant.DOWNLOAD_FOLDER);
								//.downloadFile(uToken, packageID, fileName, targetDir, startByte)
								
								File returnFile = new File(downloadFolder + Constant.TC_UPLOAD_FOLDER+"/"+filename);
								System.out.println("########"+downloadFolder +Constant.TC_UPLOAD_FOLDER+"/"+filename+"########");
								if (returnFile.exists()) {
									return returnFile;
								}
								
								break outer;
							}
						}
					}
				}
			}
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param msg
	 */
	private void pushNotificationsToiOSdevices(String msg) {
		InputStream keystore  = TrucoreMSServiceImpl.class.getClassLoader().getResourceAsStream("com/rocket/tms/service/RLinkAPNS.p12");
        try {
			Push.alert(msg, keystore, "123456", false, IOStoken);
		} catch (CommunicationException e) {
			e.printStackTrace();
		} catch (KeystoreException e) {
			e.printStackTrace();
		} finally {
			try {
				keystore.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String argz[]) {
		RespGetPartnerJsonObj respGetPartnerJsonObj = new RespGetPartnerJsonObj();
		RespPartner partner1 = new RespPartner();
		partner1.username = "xxu";
		partner1.company = "rocket";
		RespPartner partner2 = new RespPartner();
		partner2.username = "xxu";
		partner2.company = "rocket";
		respGetPartnerJsonObj.partners.add(partner1);
		respGetPartnerJsonObj.partners.add(partner2);
		String result = JsonHelper.toJSON(respGetPartnerJsonObj);
		System.out.println(result);
	}
	
}
