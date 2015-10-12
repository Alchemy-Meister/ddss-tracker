# Makefile for DDSS deliverables
TEX=pdflatex
TFLAGS=\\nonstopmode\\input
BIB=bibtex
FNAME=ddss-e1
BNAME=bib


all: $(FNAME).pdf

$(FNAME).aux: $(FNAME).tex
	$(TEX) $(TFLAGS) $(FNAME).tex

$(FNAME).pdf: $(FNAME).aux $(BNAME).bib
	$(BIB) $(FNAME).aux
	$(TEX) $(TFLAGS) $(FNAME).tex
	$(TEX) $(TFLAGS) $(FNAME).tex

clean:
	$(RM) *.log *.aux *.out *.bbl *.blg
