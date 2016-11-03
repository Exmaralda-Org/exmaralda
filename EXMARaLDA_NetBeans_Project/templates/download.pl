#!/usr/bin/perl -w

###############################################################
# Werte das Bestell-AGD Formular aus und versendet den Auftrag.
# Autor: Wolfgang Knobloch
# Stand: 17.03.2009
###############################################################

###############################################################
# Update
# Autor: Carolin Haas
# Stand: 23.02.2012
# &auml;nderungen: 
#- Anstatt HTML f&uuml;r Men&uuml;s auszuschreiben, wird die entsprechende Datei ausgegeben
#- das Skript download_en.pl wurde gel&ouml;scht und es gibt nun eine Variabel, die die Sprachwahl enth&auml;lt ($language)
###############################################################


use Mail::Sendmail;

#use strict;	

use CGI;

my $query = new CGI;

my $password;

my $valid_password = 0;

print "Content-type:text/html; charset=UTF-8\n\n";


###############################################################
#
#  Teste auf alle notwendigen Felder der Eingabe;
#
###############################################################

$my_file="benutzer.csv";

$password = $query->param("password");
$language = $query->param("language");

open(USER,"$my_file") || die("This file will not open!");

while (my $line = <USER>) { 
	
	@felder = split(/;/, $line);
	
	if (@felder[0] eq $password)
		{ 
		
		 $valid_password = 1;
		}
	
}

close(USER);


if ($valid_password == 1){
	open(HEADER, "../htdocs/menu_header.shtml") or die "file open error";
	$header="";
	while (defined ($_line = <HEADER>)) {
		$_line =~ s|css/main.css|../css/main.css|g;
		$header = $header . $_line;
	}
	close HEADER;

	open(KOPF, "../htdocs/menu_kopf.shtml");
	$kopf="";
	while (defined ($_line = <KOPF>)) {
		$_line =~ s|bilder/hintergrund-titel.jpg|../bilder/hintergrund-titel.jpg|g;
		$_line =~ s|bilder/monitor.jpg|../bilder/monitor.jpg|g;
		$_line =~ s|href="(.*?).shtml"|href="../$1.shtml"|g;
		$kopf = $kopf . $_line;
	}
	close KOPF;

	open(NAVI, "../htdocs/menu_navi.shtml");
	$navi="";
	while (defined ($_line = <NAVI>)) {
		$_line =~ s|href="(.*?).shtml"|href="../$1.shtml"|g;
		$_line =~ s|src="bilder/(.*?)"|src="../bilder/$1"|g;
		$navi = $navi . $_line;
	}
	close NAVI;

	open(RECHTS, "../htdocs/menu_navi_rechts.shtml");
	$rechts="";
	while (defined ($_line = <RECHTS>)) {
		$_line =~ s|href="(.*?).shtml"|href="../$1.shtml"|g;
		$rechts = $rechts . $_line;
	}
	close RECHTS;

	open(FUSS, "../htdocs/menu_fuss.shtml");
	$fuss="";
	while (defined ($_line = <FUSS>)) {
		$_line =~ s|src="bilder/(.*?)"|src="../bilder/$1"|g;
		$fuss = $fuss . $_line;
	}
	close FUSS;

	print $header;
	print "<body>";
	print $kopf;
	print $navi;

	if ($language eq "de"){
	
		print qq|
			<div id="inhalt">
				<h1>FOLKER Download</h1><br />
				<p>
                                    Sie k&ouml;nnen hier die Version 1.1 von FOLKER herunterladen, sowie das aktuelle Preview f&uuml;r Version 1.2 
                                    (inklusive OrthoNormal). Dar&uuml;ber hinaus finden Sie hier Anleitungen, Mustertranskripte und ein Tastatur-Layout f&uuml;r 
                                    das Transkribieren nach GAT 2.
                                </p>
				<hr/>
				<p>
                                    <b>Programm (aktuelle offizielle Version 1.1):</b><br/>
                                    Download f&uuml;r Windows 7,8,10 (98/ME/NT/2000/XP/Vista)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=folker_setup.exe">[PC-Version (Installations-Datei)]</a>
                                </p>
				<p>
                                    Download f&uuml;r Intel MAC (MAC OS 10.4 oder h&ouml;her)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=folker.dmg">[Mac-Version (Disk Image)]</a>
                                </p>
				<p>
                                    <i><font size="-1">Wenn man bei der MAC-Version Probleme bei der HTML-Ausgabe von Transkripten hat, 
                                    sollte man den gew&uuml;nschten Dateityp nochmals explizit anw&auml;hlen (nicht den voreingestellten &uuml;bernehmen). 
                                    Dieses Problem ist in der n&auml;chsten Version von FOLKER behoben.</font></i>
                                </p>
				<hr/>
				<p><b>Programm (aktuelles Preview f&uuml;r Version 1.2):</b><br/>Download f&uuml;r Windows 7,8,10 (98/ME/NT/2000/XP/Vista)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=folker_preview_setup.exe">[PC-Version (Installations-Datei)]</a></p>
				<p>Download f&uuml;r Intel MAC (MAC OS 10.6 oder h&ouml;her)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=folker_preview.dmg">[Mac-Version Folker (Disk Image)]</a>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=orthonormal_preview.dmg">[Mac-Version OrthoNormal (Disk Image)]</a>
                                </p>
				<p>Download f&uuml;r Linux<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=folker_preview.tar.gz">[Linux-Version (Gnuzipped Tarball)]</a>
                                </p>
				<p>
                                    <i><font size="-1">Diese Version stammt vom April 21 2016 (1307). Alle &Auml;nderungen zu den Vorg&auml;ngerversionen sind 
                                    <a href="http://agd.ids-mannheim.de/folker_changes.html">auf dieser Seite</a> beschrieben.</font></i>
                                </p>
				<hr/>
				<p>
                                    <b>Dokumentation f&uuml;r Anwender (Version 1.1):</b><br>
                                    Schnellstart-Anleitung (1 Seite)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER-Schnellstart.pdf" target="_blank">[Schnellstart (PDF)]</a>
                                </p>
				<p>
                                    Handbuch (Version 1.1)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER-Transkriptionshandbuch.pdf" target="_blank">[Handbuch (PDF)]</a>
                                </p>
				<p>
                                    Handbuch (Version Preview 1.2)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER-Transkriptionshandbuch_preview.pdf" target="_blank">[Handbuch (PDF)]</a>
                                </p>
				<p>
                                    Demonstration von Syntaxbeispielen<br/>
                                    (Dieses Schulungs-Transkript wird im Handbuch-Abschnitt 4.2 erl&auml;utert)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=syntaxbeispiele2.flk" target="_blank">[Transkript (FLK)]</a>
                                </p>
				<hr/>
				<p>
                                    <b>Dokumentation f&uuml;r Entwickler (Version 1.1):</b><br>FOLKER-Datenmodell<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER-Datenmodell.pdf" target="_blank">[Datenmodell (PDF)]</a>
                                </p>
				<p>
                                    XML-Schema<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=Folker_Schema.xsd" target="_blank">[XML-Schema (XSD)]</a>
                                </p>
				<hr/>
				<p>
                                    <b>Mustertranskripte:</b><br/>
                                    1.) "Der widerliche Kerl" (<a href="http://www.gespraechsforschung-ozs.de/heft2009/px-gat2.pdf" target="_blank">
                                    Selting/Auer et al. 2009</a>)<br/>Zur Audiodatei (in reduzierter Qualit&auml;t) werden in einem ZIP-Archiv (4 MB) ein 
                                    Minimaltranskript nach GAT-2-Konventionen und ein cGAT-Basistranskript angeboten. 
                                    Alle Dateien sollten in dasselbe Verzeichnis entpackt werden.
                                    F&uuml;r das cGAT-Basistranskript sollte FOLKER auf die zugeh&ouml;rige Transkriptstufe 3 eingestellt werden.<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER-Mustertranskript_1.zip" target="_blank">
                                    [FOLKER-Mustertranskript 1 (ZIP-Archiv)]</a><br/><br/>
                                    2.) Ausschnitt aus einer Maptask-Aufnahme aus dem Projekt "Deutsch heute"<br>
                                    In einem ZIP-Archiv (7 MB) werden die Audiodatei eines 55 Sekunden langen Ausschnitts und das Minimaltranskript angeboten. 
                                    Die Dateien sollten in dasselbe Verzeichnis entpackt werden.<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER-Mustertranskript_2.zip" target="_blank">
                                    [FOLKER-Mustertranskript 2 (ZIP-Archiv)]</a>
                                </p>				
				<hr/>
				<p><b>Tastatur-Layout f&uuml;r das Transkribieren nach GAT 2:</b><br>Tastatur-Layouts legen die Tastenbelegung fest und m&uuml;ssen auf Betriebssystem-Ebene eingerichtet werden.<br>Wir bieten hier ein Tastatur-Layout an, das neben der deutschen Tastaturbelegung Tastenkombinationen zur bequemen Eingabe von Symbolen enth&auml;lt, die f&uuml;r die Basis- bzw. Feintranskription nach GAT 2 (<a href="http://www.gespraechsforschung-ozs.de/heft2009/px-gat2.pdf" target="_blank">Selting/Auer et al. 2009</a>) ben&ouml;tigt werden. Wir empfehlen dieses Tastatur-Layout allen GAT-2-Benutzern, die regelm&auml;&szlig;ig mit FOLKER oder einer Textverarbeitung Basis- oder Feintranskripte erstellen.<br>Die Tastatur-Layouts lassen sich in allen unicodef&auml;higen Programmen verwenden, also insbesondere in FOLKER und in MS Word. Voraussetzung ist jeweils, dass in diesen Programmen ein Schriftsatz eingestellt ist, der die entsprechenden Symbole enth&auml;lt. Dies ist beispielsweise gegeben f&uuml;r den Schriftsatz Arial Unicode MS, der in den meisten Distributionen von MS Office enthalten ist.<br><br></p>
				<p>Download f&uuml;r Windows XP/Vista/Windows7 (Vista und Windows 7 jeweils f&uuml;r 32bit und 64bit)<br><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=GAT-Keyboard-Installation.zip" target="_blank">[PC-Version]</a><br><br></p>		
				<p>Download f&uuml;r MAC OS X (10.5 oder 10.6)<br><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=GAT.keylayout.xml" target="_blank">[Mac-Version]</a><br><br></p>
				<p><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=Dokumentation.pdf" target="_blank">[Installationsanweisungen und Dokumentation]</a></p>
			</div>
			|;
		
	} else {
	
		print qq|
			<div id="inhalt">
				<h1>FOLKER Download</h1><br />
				<p>Here you can download version 1.1 of FOLKER and an up-to-date preview of version 1.2 (including OrthoNormal). You will also find here instructions and a keyboard layout for transcribing following GAT-2 transcription conventions.</p>
				<hr/>
				<p><b>Program (current official version 1.1):</b><br/>Download for Windows 7,8,10 (98/ME/NT/2000/XP/Vista)<br/><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=folker_setup.exe">[PC version]</a></p>
				<p>Download for Intel MAC (MAC OS 10.4 or above)<br/><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=folker.dmg">[Mac version]</a></p>
				<p><i><font size="-1">As for the MAC version: If you encounter problems with the HTML output of transcripts, you should choose the desired file type explicitly instead of simply using the default one. This problem will be solved in the next FOLKER version.</font></i></p>
				<hr/>
				<p><b>Program (current preview of version 1.2):</b><br/>Download for Download for Windows 7,8,10 (98/ME/NT/2000/XP/Vista)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=folker_preview_setup.exe">[PC version]</a></p>
				<p>Download for Intel MAC (MAC OS 10.6 or higher)<br/>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=folker_preview.dmg">[Mac version Folker]</a>
                                    <a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=orthonormal_preview.dmg">[Mac version OrthoNormal]</a>
                                </p>
				<p><i><font size="-1">This version is from April 21 2016 (1307). All changes with respect to previous versions are described <a href="http://agd.ids-mannheim.de/folker_changes.html">on this page</a>.</font></i></p>
				<hr/>
				<p><b>Documentation for users (for version 1.1):</b><br>Quick start tutorial (1 page)<br/><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER_Quick_start_tutorial_EN.pdf" target="_blank">[Quick start tutorial]</a></p>
				<p>Transcription manual (77 pages)<br><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER-transcription_manual.pdf" target="_blank">[Transcription manual]</a></p>
				<p>Demonstration of the FOLKER syntax by a sample transcript<br/><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=syntax_examples.flk" target="_blank">[Transcript]</a></p>
				<hr/>
				<p><b>Documentation for developers (for version 1.1):</b><br>FOLKER data model<br/><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER-Datenmodell.pdf" target="_blank">[Data model]</a></p>
				<p>XML schema<br/><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=Folker_Schema.xsd" target="_blank">[XML schema]</a></p>
				<hr/>
				<p><b>Sample transcript:</b><br/>"Scientologist Tommy Davis responds to Anonymous" (CNN, American Morning, 08.05.2008, CNN's John Roberts interviews Scientologist Tommy Davis, cf. <a href="http://www.youtube.com/watch?v=5rkbYaarlxg" target="_blank">http://www.youtube.com/watch?v=kSS178Q-4eo</a>). In this ZIP archive (16 MB) you'll find the FOLKER transcript and a WAV audio file in reduced quality. Both files should be extracted to the same directory.<br/><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=FOLKER_sample_transcript.zip" target="_blank">[FOLKER sample transcript]</a></p>
				<hr/>
				<p><b>Keyboard layout for transcribing following GAT-2 transcription conventions:</b><br>Keyboard layouts have to be installed on the operating system level.<br>Here we present a keyboard layout which – apart from the German keyboard – offers keyboard shortcuts for a convenient entry of symbols needed for base or fine transcripts according to GAT 2 (<a href="http://www.gespraechsforschung-ozs.de/heft2009/px-gat2.pdf" target="_blank">Selting/Auer et al. 2009</a>). This keyboard layout is recommended for all GAT-2 users who regularly work on base or fine transcripts using FOLKER or a word processing software.<br>The keyboard layouts are usable in all programs that are Unicode ready, thus in particular in FOLKER and in Microsoft Word. For those programs a font must be chosen which contains the appropriate symbols. This is e.g. granted for the Arial Unicode MS font which is included in most MS Office distributions.<br><br></p>
				<p>Download for Windows XP/Vista/Windows7 (Vista and Windows 7 for 32bit and 64bit respectively)<br><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=GAT-Keyboard-Installation.zip" target="_blank">[PC version]</a><br><br></p>		
				<p>Download for MAC OS X (10.5 or 10.6)<br><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=GAT.keylayout.xml" target="_blank">[Mac version]</a><br><br></p>
				<p><a href="/cgi-bin/download_file.pl?benutzer=$password&datei_name=Dokumentation.pdf" target="_blank">[Installation instruction and documentation]</a> (in German)</p>
			</div>
		|;
	
	}

	print $rechts;
	print $fuss;

	print "</body>";
	print "</html>";
} else {
	print qq|
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			<meta http-equiv="refresh" content="0; URL=../folker_download_fehler.shtml" />
		</head>
	</html>
	|;






}