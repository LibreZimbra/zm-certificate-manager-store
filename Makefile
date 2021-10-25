# SPDX-License-Identifier: AGPL-3.0-or-later

ANT_TARGET = jar

all: build-ant-autover

include build.mk

install:
	$(call mk_install_dir, lib/ext/com_zimbra_cert_manager)
	cp build/zm-certificate-manager-store*.jar $(INSTALL_DIR)/lib/ext/com_zimbra_cert_manager/com_zimbra_cert_manager.jar

clean: clean-ant
