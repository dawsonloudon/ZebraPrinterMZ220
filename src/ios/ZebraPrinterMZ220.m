#import "ZebraPrinterMZ220.h"
#import <Cordova/CDV.h>
#import <ExternalAccessory/ExternalAccessory.h>
#import "MfiBtPrinterConnection.h"

@implementation ZebraPrinterMZ220

- (void)findPrinters:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
}

- (void)getDevices:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    EAAccessoryManager *manager = [EAAccessoryManager sharedAccessoryManager];

    NSDictionary *forOut;
    self.bluetoothPrinters = [[NSMutableArray alloc] initWithArray:manager.connectedAccessories];
    NSArray *printers = [[NSArray alloc] initWithArray:self.bluetoothPrinters];
    int counter = [printers count];
    NSString *keys[counter];
    NSString *values[counter];
    for(int i = 0; i < counter; i++) {
        EAAccessory *device = [printers objectAtIndex:i];
        keys[i] = device.name;
        values[i] = device.serialNumber;
    }
    forOut = [NSDictionary dictionaryWithObjects:(id *)values forKeys:(id *)keys count:counter];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:forOut];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)openConnection:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    self.printerAddress = [command.arguments objectAtIndex:0];
    self.thePrinterConn = [[MfiBtPrinterConnection alloc] initWithSerialNumber:self.printerAddress];
    BOOL success = [self.thePrinterConn open];
    if(success){
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    else{
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)closeConnection:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    [self.thePrinterConn close];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"true"];
}

- (void)print:(CDVInvokedUrlCommand*)command
{
    [self performSelectorInBackground:@selector(runPrint:) withObject:command];
}

- (void)runPrint:(CDVInvokedUrlCommand*)command
{
    NSString *cpclData = [command.arguments objectAtIndex:0];
    NSError *error = nil;
    BOOL success = [self.thePrinterConn write:[cpclData dataUsingEncoding:NSUTF8StringEncoding] error:&error];
    if(success){
        [self performSelectorOnMainThread:@selector(afterPrintSuccess:) withObject:command waitUntilDone:NO];
    }
    else{
        [self performSelectorOnMainThread:@selector(afterPrintFailure:) withObject:command waitUntilDone:NO];
    }
}

- (void)afterPrintSuccess:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)afterPrintFailure:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end