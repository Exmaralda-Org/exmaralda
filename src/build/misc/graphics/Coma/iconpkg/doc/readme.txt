iPhone & iPod Touch usage:

Apple updated thier devices to allow users to add website links to their home screen. 
Officially called 'WebClip Bookmarks' by Apple, these home screen icons are created by 
taking a thumbnail screenshot of your current view on that website. Pretty cool, but... 
Soon enough, they all look the same and you are identifying them by their label text 
instead of the icon. But Apple has provided web site owners the ability to specify an 
icon to be used in-place of the screenshot. The implementation is simple. When the 
iPhone's browser visits a page, it will look for a file in the root of the site called:

apple-touch-icon.png

You can also explicity specify which icon should be used when the iPhone's browser
visits your site by specifying a 'link' tag in the head portion of the page's html.
This implementation is very similiar to the favicon.ico usage. Here is the proper
usage of the iPhone /iPod Touch LINK element:

<link rel="apple-touch-icon" href="/custom-icon-name.png" />


Favicon & .ico usage:

The favicon is a small icon which helps to brand your website visually. The icon
will often appear in the browser's title or url. When a visitor bookmarks your site
or otherwise creates a shortcut/link to it, your icon will replace the default one
used by the browser or os (in case of adding to a windows desktop)
You can use pretty much any web compatible image format to enable this feature and
often developers will use a .png for its transparency benefits. The recommended
file format is still .ico however and offers some benefits over .png when used in
this context. As an .ico file, your windows visitors will have the best integration
when linking to your site.

Many browsers will automatically display the icon if you simply have a file named:

favicon.ico 

in the root of the website. You can specify a different filename and/or
location by using one of the following link elements.

For .ico :
<link rel="icon" type="image/vnd.microsoft.icon" href="/somepath/image.ico" />

For .gif :
<link rel="icon" type="image/gif" href="/somepath/image.gif" />

For .png :
<link rel="icon" type="image/png" href="/somepath/image.png" />

