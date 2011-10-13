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
