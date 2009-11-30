package xmpp.messaging.domain;

import utils.HashUtil;

/**
 * Stores information about person who is participating in private conversation
 * or in a group chat. Class is <b>immutable</b>. Implements Value Object
 * pattern.
 * 
 * @author tillias
 * 
 */
public final class ParticipantInfo {

    /**
     * Creates new instance using given jabber identifier and fully qualified
     * adress
     * 
     * @param jabberID
     *            Jabber identifier of participant
     * @param adress
     *            Fully qualified adress of participant
     * @throws NullPointerException
     *             Thrown if any argument passed to method is null
     */
    public ParticipantInfo(String jabberID, String adress)
	    throws NullPointerException {
	if (jabberID == null || adress == null)
	    throw new NullPointerException(
		    "Arguments passed to constructor can't be null");

	this.jabberID = jabberID;
	this.adress = adress;
    }

    /**
     * Copy constructor
     * 
     * @param i
     *            Source {@link ParticipantInfo} instance which will be used to
     *            create current one
     * @throws NullPointerException
     *             Thrown if argument passed to this method is null
     */
    public ParticipantInfo(ParticipantInfo i) throws NullPointerException {
	if (i == null)
	    throw new NullPointerException(
		    "ParticipantInfo copy constructor's argument can't be null");

	adress = i.getAdress();
	jabberID = i.getJabberID();
    }

    /**
     * Gets jabberID of participant
     * 
     * @return JabberID of participant
     */
    public String getJabberID() {
	return jabberID;
    }

    /**
     * Gets address of participant
     * 
     * @return Address of participant
     */
    public String getAdress() {
	return adress;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("JabberID: ");
	sb.append(jabberID);
	sb.append('\n');
	sb.append("Adress: ");
	sb.append(adress);
	return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;

	if (!(obj instanceof ParticipantInfo))
	    return false;

	ParticipantInfo pi = (ParticipantInfo) obj;

	return pi.getAdress().equals(getAdress())
		&& pi.getJabberID().equals(getJabberID());

    }

    @Override
    public int hashCode() {
	if (fHashCode == 0) {
	    fHashCode = HashUtil.SEED;
	    fHashCode ^= HashUtil.hashString(fHashCode, jabberID);
	    fHashCode ^= HashUtil.hashString(fHashCode, adress);
	}
	return fHashCode;
    }

    String jabberID;
    String adress;
    int fHashCode;
}
