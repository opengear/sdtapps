SRC=SDTConnector
DIST=$(SRC)/dist

all:
	cd $(SRC) && ant

clean:
	cd $(SRC) && ant clean

dist:
	bash $(SRC)/tools/sdt-dist

install:
	@if [ -e "$(ROOTDIR)" ]; then \
		mkdir -p $(DESTDIR)/home/httpd/sdtconnector; \
		cp \
			$(DIST)/SDTConnector.jar \
			$(DIST)/launch.jnlp \
			$(SRC)/images/program.png \
			$(SRC)/images/sdtconnector.png \
			$(DESTDIR)/home/httpd/sdtconnector; \
		cp -r $(DIST)/lib $(DESTDIR)/home/httpd/sdtconnector; \
	else \
		echo "make dist to build installation packages"; \
	fi

VERSION := $(shell awk -F\" '/VERSION/{print $$2}' $(SRC)/src/sdtconnector/SDTConnector.java)
sdtapps-$(VERSION).tar.gz:
	git archive --prefix=sdtapps-$(VERSION)/ --format=tar HEAD | gzip -9 >$@

sdtapps-dist: sdtapps-$(VERSION).tar.gz
