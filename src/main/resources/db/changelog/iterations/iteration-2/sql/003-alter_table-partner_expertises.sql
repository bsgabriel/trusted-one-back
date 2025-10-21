ALTER TABLE partner_expertises RENAME COLUMN available_for_referrals TO available_for_referral;

COMMENT ON COLUMN partner_expertises.available_for_referral IS 'Indicates whether the partner accepts referrals/indications for this specific expertise';